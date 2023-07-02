package ProyectoIntegrador
import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

object GeneracionScriptsCanton extends App{
  val reader = CSVReader.open(new File("C:\\Users\\USUARIO\\Downloads\\cantonProvincia.csv"))(new DefaultCSVFormat {
    override val delimiter = ';'
  }) //Provincia-canton

  val reader2 = CSVReader.open(new File("C:\\Users\\USUARIO\\Downloads\\OneDrive_1_24-6-2023\\Bases_de_Datos_ENEMDU_diciembre_2022\\2_BDD_DATOS_ABIERTOS_ENEMDU_2022_12_CSV_V2\\enemdu_persona_2022_12.csv"))(new DefaultCSVFormat {
    override val delimiter = ';'
  })


  val data = reader.allWithHeaders()
  reader.close()

  val data2 = reader2.allWithHeaders()
  reader2.close()


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

  case class Canton(
                   idCanton: Int,
                   nv_pobreza : Int,
                   nombreCanton: String,
                   idProvincia: Long
                   )

    val cantonData = data
    .map(row => (row("Código Cantón").toInt, row("Nombre del Cantón"), row("Código Provincia")))

  val pobreza = data2
    .map(row => (row("p21"), row("ciudad")))
    .groupBy(identity).view.mapValues(_.size).toList
    .filter(_._1._1.equals("12")).sorted

  val union = cantonData.flatMap(canton => pobreza.find(_._1._2.contains(canton._1 + canton._3))
  .map(pobre => (canton._1, pobre._2, canton._2.replaceAll("Ã‘", "Ñ"), canton._3.toInt))).distinct

  println(cantonData.map(x => x._1 + x._3))
  println(pobreza.map(_._1._2.substring(0,3)))
  println(union)

  val SQL_PATTERN =
    """
      |INSERT INTO viviendas.canton (idCanton, nv_Pobreza, nombreCanton, Provincia_idProvincia)
      |VALUES
      |(%d, %d,"%s", %d);
      |""".stripMargin


  val scriptData = union
    .map(canton => SQL_PATTERN.formatLocal(java.util.Locale.US,
      canton._1,
      canton._2,
      escapeMysql(canton._3),
      canton._4))

  val ScriptFile = new File("C:\\Users\\USUARIO\\Desktop\\Canton_insert.sql")
  if (ScriptFile.exists()) ScriptFile.delete()

  scriptData.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\Canton_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))


}
