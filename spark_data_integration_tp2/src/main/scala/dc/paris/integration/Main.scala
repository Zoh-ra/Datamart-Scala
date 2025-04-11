package dc.paris.integration

import org.apache.spark.sql.SparkSession
import io.github.cdimascio.dotenv.Dotenv

object Main extends App {
  val dotenv = Dotenv.load()

  val accessKey = dotenv.get("ACCESS_KEY_ID")
  val secretKey = dotenv.get("SECRET_ACCESS_KEY")

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

  // Define the path to the Parquet file to load
  val fileUrl = "s3a://datalake/processed/yellow_tripdata_2024-12.parquet"

  // Load Parquet file into Spark DataFrame
  val df = spark.read.parquet(fileUrl)

  df.printSchema()

  // Define PostgreSQL connection properties
  val jdbcUrl = "jdbc:postgresql://localhost:15432/dbwarehouse"
  val dbProperties = new java.util.Properties()
  dbProperties.setProperty("user", "postgres")
  dbProperties.setProperty("password", "admin")
  dbProperties.setProperty("driver", "org.postgresql.Driver")

  // Write the DataFrame to PostgreSQL (Data Warehouse)
  df.write
    .mode("append")
    .jdbc(jdbcUrl, "nyc_yellow_taxi_trips", dbProperties)

  println("Data ingested successfully into PostgreSQL Data Warehouse")

  // Stop Spark session
  spark.stop()
}
