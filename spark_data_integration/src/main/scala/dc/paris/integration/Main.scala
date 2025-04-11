package dc.paris.integration

import org.apache.spark.sql.SparkSession
import java.net.URL
import java.io.File
import java.nio.file.{Files, Paths, StandardCopyOption}


object Main extends App {
  val spark: SparkSession = SparkSession
    .builder()
    .appName("Data Integration")
    .master("local[*]")
    .config("fs.s3a.access.key", "feYIrRkftrx645QsECKw") // A renseigner
    .config("fs.s3a.secret.key", "2RDLb99JSmwd8Zj1JcNPvK6t2LbaQnHSDp1cctRW") // A renseigner
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

    // Télécharger le fichier en local
    val in = new URL(url).openStream()
    Files.copy(in, Paths.get(localPath), StandardCopyOption.REPLACE_EXISTING)
    println(s"Fichier téléchargé : $localPath")

    // Lire avec Spark
    val df = spark.read.parquet(localPath)
    df.write.parquet(s"$outputDir/processed_$fileName")
    println(s"Fichier traité et sauvegardé dans : $outputDir/processed_$fileName")
  }

  spark.stop()
}
