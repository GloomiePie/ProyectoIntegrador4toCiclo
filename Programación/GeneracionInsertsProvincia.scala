package ProyectoIntegrador

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

object GeneracionInsertsProvincia extends App{

  val reader = CSVReader.open(new File("C:\\Users\\USUARIO\\Downloads\\part-00000-25c9083d-50b7-4301-a80b-6284e2689d18-c000 (1).csv"))(new DefaultCSVFormat {
    override val delimiter = ';'
  })
  val reader2 = CSVReader.open(new File("C:\\Users\\USUARIO\\Downloads\\OneDrive_1_24-6-2023\\provincias.inec.csv"))

  val reader3 = CSVReader.open(new File("C:\\Users\\USUARIO\\Downloads\\mdg_homicidiosintencionales_pm_2023_enero_mayo.csv"))(new DefaultCSVFormat {
    override val delimiter = ';'
  })


  val data = reader.allWithHeaders()
  reader.close()

  val data2 = reader2.allWithHeaders()
  reader2.close()

  val data3 = reader3.allWithHeaders()
  reader3.close()

  def escapeMysql(text: String): String = text
    .replaceAll("\\\\", "\\\\\\\\")
    .replaceAll("\b", "\\\\b")
    .replaceAll("\n", "\\\\n")
    .replaceAll("\r", "\\\\r")
    .replaceAll("\t", "\\\\t")
    .replaceAll("\\x1A", "\\\\Z")
    .replaceAll("\\x00", "\\\\0")
    .replaceAll("'", "\\\\'")
    .replaceAll("\"", "\\\\\"")

  case class Provincia(
                      idProvincia: Int,
                      nombreProvincia: String,
                      dispoHospitalaria: Int,
                      homicidios: Int
                      )


  val provinciaData = data
    .map(row => (row("Codigo").toInt,
      row("Canton")))

  val dispoHospi = data2
    .map(row => (row("disponibilidad_hospitalizacion").toInt,
      row("inec_provincia_id").toInt)).groupBy(_._2).toList.sortBy(_._1)

  val homicidios = data3
    .map(row => (row("Cod_prov"), row("Provincia")))
    .groupBy(identity).view.mapValues(_.size).toMap


  val union = provinciaData.flatMap(provincia => dispoHospi.find(x => x._1 == provincia._1)
    .map(dispoH => (provincia._1, provincia._2, dispoH._2.map(_._1).max)))


  val union2 = union.flatMap(provincia => homicidios.find(_._1._1.toInt == provincia._1)
  .map(homi => (provincia._1, provincia._2, provincia._3, homi._2)))


 val unionData = union2
    .map(row => Provincia(
      row._1,
      row._2,
      row._3,
      row._4
    ))


  val SQL_PATTERN =
    """
      |INSERT INTO viviendas.provincia (idProvincia, nombreProvincia, dispoHospitalaria, Homicidios)
      |VALUES
      |(%d, "%s", %d, %d);
      |""".stripMargin



 val scriptData = unionData
    .map(provincia => SQL_PATTERN.formatLocal(java.util.Locale.US,
      provincia.idProvincia,
      escapeMysql(provincia.nombreProvincia),
      provincia.dispoHospitalaria,
      provincia.homicidios))

  val ScriptFile = new File("C:\\Users\\USUARIO\\Desktop\\provincias_insert.sql")
  if (ScriptFile.exists()) ScriptFile.delete()

  scriptData.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\provincias_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))



}
