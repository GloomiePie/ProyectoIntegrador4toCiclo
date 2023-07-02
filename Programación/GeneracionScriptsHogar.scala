package ProyectoIntegrador

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

object GeneracionScriptsHogar extends App{

  val reader = CSVReader.open(new File("C:\\Users\\USUARIO\\Downloads\\enemdu_vivienda_hogar_2023_I_trimestre.csv"))(new DefaultCSVFormat {
    override val delimiter = ';'
  })
  val data = reader.allWithHeaders()
  reader.close()

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

  case class Hogar(
                  pagoMensual: Int,
                  incluye_agua: String,
                  numDormitorios: Int,
                  numCuartosNegocio: Int,
                  numCuartos: Int,
                  tipoVivienda: String,
                  cantidadHogar: Int,
                  disponeHogarCocina: String,
                  codHogar: String,
                  idVivienda: String
                  )

  val hogar = data
    .map(row => Hogar(row("vi141").toInt, row("vi10") match {
      case "1" => "Red Pública"
      case "2" => "Pila o llave pública"
      case "3" => "Otra fuente por tubería"
      case "4" => "Carro repartidor/triciclo"
      case "5" => "Pozo"
      case "6" => "Río, vertiente o acequia"
      case "7" => "Otro"
    }, row("vi07").toInt, row("vi07a").toInt, row("vi06").toInt,
    row("vi02") match {
      case "1" => "Casa o villa"
      case "2" => "Departamento"
      case "3" => "Cuartos en casa de inquilinato"
      case "4" => "Mediagua"
      case "5" => "Rancho, covacha"
      case "6" => "Choza"
      case "7" => "Otro"
    }, row("hogar").toInt, row("vi07b") match {
      case "1" => "Si"
      case "2" => "No"
    }, row("id_hogar"), row("id_vivienda")))

  val SQL_PATTERN =
    """
      |INSERT INTO viviendas.hogar (pagoMensual, incluye_agua, numDormitorios,
      |numCuartosNegocio, numCuartos, tipoVivienda, cantidadHogar, disponeHogarCocina,
      |codHogar, Vivienda_idVivienda)
      |VALUES
      |(%d, "%s", %d, %d, %d, "%s", %d, "%s", %s, %s);
      |""".stripMargin

  val scriptData = hogar
    .map(hogar => SQL_PATTERN.formatLocal(java.util.Locale.US,
      hogar.pagoMensual,
      hogar.incluye_agua,
      hogar.numDormitorios,
      hogar.numCuartosNegocio,
      hogar.numCuartos,
      hogar.tipoVivienda,
      hogar.cantidadHogar,
      hogar.disponeHogarCocina,
      hogar.codHogar,
      hogar.idVivienda
    ))

  val ScriptFile = new File("C:\\Users\\USUARIO\\Desktop\\hogar_insert.sql")
  if (ScriptFile.exists()) ScriptFile.delete()

  scriptData.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\hogar_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))

  //vi06, 07, 07a cuartos
  //vi10 agua
  //vi02 tipovivienda
  //hogar cantidadHogar
  //vi07b disponehogarcocina
  //idHogar codHogar
}
