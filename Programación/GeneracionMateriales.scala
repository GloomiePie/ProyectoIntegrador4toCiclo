import com.github.tototoshi.csv._

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

object GeneracionMateriales extends App {
  val reader = CSVReader.open(new File("D:\\Downloads\\enemdu_vivienda_hogar_2023_I_trimestre\\enemdu_vivienda_hogar_2023_I_trimestre.csv"))(new DefaultCSVFormat {
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

  case class Vivienda(
                       tipoVivienda: String,
                       materialCocina: String,
                       materialTechoCubierta: String,
                       estadoParedes: String,
                       estadoTecho: String,
                       materialPiso: String,
                       estadoPiso: String,
                       materialParedes: String,
                     )

  val viviendaData = data
    .map(row => Vivienda(
      row("vi02") match {
        case "1" => "Casa o villa"
        case "2" => "Departamento"
        case "3" => "Cuartos en casa de inquilinato"
        case "4" => "Mediagua"
        case "5" => "Rancho, covacha"
        case "6" => "Choza"
        case "7" => "Otra"
      },
      row("vi08") match {
        case "1" => "Gas"
        case "2" => "Leña, carbón"
        case "3" => "Electricidad"
        case "4" => "Otro"
      },
      row("vi03a") match {
        case "1" => "Hormigón (losa, cemento)"
        case "2" => "Fibrocemento,asbesto (eternit, eurolit)"
        case "3" => "Zinc, Aluminio"
        case "4" => "Teja"
        case "5" => "Palma, paja u hoja"
        case "6" => "Otro Material"
      },
      row("vi05b") match {
        case "1" => "Bueno"
        case "2" => "Regular"
        case "3" => "Malo"
      },
      row("vi03b") match {
        case "1" => "Bueno"
        case "2" => "Regular"
        case "3" => "Malo"
      },
      row("vi04a") match {
        case "1" => "Duela, parquet, tablón tratado o piso flotante"
        case "2" => "Cerámica, baldosa, vinil o porcelanato"
        case "3" => "Mármol o marmetón"
        case "4" => "Ladrillo o cemento"
        case "5" => "Tabla / tablón no tratado"
        case "6" => "Caña"
        case "7" => "Tierra"
        case "8" => "Otro Material"
      },
      row("vi04b") match {
        case "1" => "Bueno"
        case "2" => "Regular"
        case "3" => "Malo"
      },
      row("vi05a") match {
        case "1" => "Hormigón/Ladrillo o Bloque"
        case "2" => "Asbesto/Cemento (Fibrolit)"
        case "3" => "Adobe o Tapia"
        case "4" => "Madera"
        case "5" => "Caña revestida o bahareque"
        case "6" => "Caña no revestida o estera"
        case "7" => "Otra Material"
      }
    )
    )

  val SQL_PATTERN =
    """
      |INSERT INTO viviendas.ServiciosBasicos (tipoVivienda, materialCocina, materialTechoCubierta, estadoParedes, estadoTecho, materialPiso, estadoPiso, materialParedes)
      |VALUES
      |("%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s");
      |""".stripMargin

  val scriptData = viviendaData
    .map(vivienda => SQL_PATTERN.formatLocal(java.util.Locale.US,
      vivienda.tipoVivienda,
      vivienda.materialCocina,
      vivienda.materialTechoCubierta,
      escapeMysql(vivienda.estadoParedes),
      vivienda.estadoTecho,
      vivienda.materialPiso,
      vivienda.estadoPiso,
      vivienda.materialParedes))

  val ScriptFile = new File("C:\\Users\\CM\\Desktop\\materiales_insert.sql")
  if (ScriptFile.exists()) ScriptFile.delete()

  scriptData.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\CM\\Desktop\\materiales_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))
}
