package dc.paris.integration

import org.apache.spark.sql.SparkSession



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
    "https://d37ci6vzurychx.cloudfront.net/trip-data/yellow_tripdata_2024-12.parquet",
    "https://d37ci6vzurychx.cloudfront.net/trip-data/yellow_tripdata_2024-11.parquet"
  )

  // Répertoire local où les fichiers seront stockés
  val outputDir = "data/raw"

  // Télécharger et enregistrer chaque fichier
  fileUrls.foreach { url =>
    val fileName = url.split("/").last
    val localPath = s"$outputDir/$fileName"

    // Lire le fichier Parquet depuis l'URL
    val df = spark.read.parquet(url)

    // Sauvegarder le fichier localement dans le répertoire 'data/raw'
    df.write.parquet(localPath)
    println(s"Fichier téléchargé et enregistré sous : $localPath")
  }

  // Arrêter le Spark session
  spark.stop()

}
