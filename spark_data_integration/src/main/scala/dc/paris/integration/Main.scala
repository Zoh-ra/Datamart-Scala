package dc.paris.integration

import org.apache.spark.sql.SparkSession
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.URI
import java.io.File
import java.nio.file.{Files, Paths, StandardCopyOption}
import io.github.cdimascio.dotenv.Dotenv


object Main extends App {
  val dotenv = Dotenv.load()

  val accessKey = dotenv.get("ACCESS_KEY_ID")
  val secretKey = dotenv.get("SECRET_ACCESS_KEY")

  val client = HttpClient.newHttpClient()

  val spark: SparkSession = SparkSession
    .builder()
    .appName("Data Integration")
    .master("local[*]")
    .config("fs.s3a.access.key", accessKey)
    .config("fs.s3a.secret.key", secretKey)
    .config("fs.s3a.endpoint", "http://localhost:9000/")
    .config("fs.s3a.path.style.access", "true")
    .config("fs.s3a.connection.ssl.enable", "false")
    .config("fs.s3a.attempts.maximum", "1")
    .config("fs.s3a.connection.establish.timeout", "1000")
    .config("fs.s3a.connection.timeout", "5000")
    .getOrCreate()

  // Liste des URLs des fichiers Parquet à télécharger
  val fileUrls = List(
    "https://d37ci6vzurychx.cloudfront.net/trip-data/yellow_tripdata_2024-10.parquet",
    "https://d37ci6vzurychx.cloudfront.net/trip-data/yellow_tripdata_2024-11.parquet",
    "https://d37ci6vzurychx.cloudfront.net/trip-data/yellow_tripdata_2024-12.parquet"
  )

  // Répertoire local où les fichiers seront stockés
  val outputDir = "../data/raw"
  new File(outputDir).mkdirs()

  fileUrls.foreach { url =>
    val fileName = url.split("/").last
    val localPath = s"$outputDir/$fileName"
    val request = HttpRequest.newBuilder()
      .uri(URI.create(url))
      .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())

    Files.copy(response.body(), Paths.get(localPath), StandardCopyOption.REPLACE_EXISTING)
    println(s"Fichier téléchargé : $localPath")

    val df = spark.read.parquet(localPath)
    df.write.parquet(s"s3a://datalake/processed/$fileName")
    println(s"Fichier traité et sauvegardé dans : $outputDir/processed_$fileName")
  }

  spark.stop()
}
