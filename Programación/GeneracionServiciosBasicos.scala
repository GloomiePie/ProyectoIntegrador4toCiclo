import com.github.tototoshi.csv._

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

object GeneracionServiciosBasicos extends App {
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
                       tipoServicioHigienico: String,
                       alternativaServicioHigienico: String,
                       servicioDucha: String,
                       tipoAlumbrado: String,
                       contieneMedidorAgua: String,
                       aguaJunta: String,
                       fuenteAgua: String,
                       sanitarioPrincipal: String,
                       aguaRecibida: String,
                       eliminacionBasura: String
                     )

  val viviendaData = data
    .map(row => Vivienda(
      row("vi09") match {
        case "1" => "Excusado y alcantarillado"
        case "2" => "Excusado y pozo séptico"
        case "3" => "Excusado y pozo ciego"
        case "4" => "Letrina"
        case "5" => "No tiene"
      },
      row("vi09a") match {
        case "1" => "Descarga directa al mar, río, lago o quebrada"
        case "2" => "Van al monte, campo, bota la basura en paquete"
        case "3" => "Usan una instalación sanitaria cercana y/o prestada"
        case _ => "Si tiene servicio higienico" // Agregar caso para valor vacío
      },
      row("vi11") match {
        case "1" => "Exclusivo del hogar"
        case "2" => "Compartido con otros hogares"
        case "3" => "No tiene"
      },
      row("vi12") match {
        case "1" => "Empresa eléctrica pública"
        case "2" => "Planta eléctrica privada"
        case "3" => "Vela, candil, mechero, gas"
        case "4" => "Ninguno"
      },
      row("vi101") match {
        case "1" => "Si"
        case "2" => "No"
        case _ => "No Informa"
      },
      row("vi102") match {
        case "1" => "Si"
        case "2" => "No"
        case _ => "No Informa"
      },
      row("vi10") match {
        case "1" => "Red pública"
        case "2" => "Pila o llave pública"
        case "3" => "Otra fuente por tubería"
        case "4" => "Carro repartidor, triciclo"
        case "5" => "Pozo"
        case "6" => "Río, vertiente, acequia"
        case "7" => "Otro"
      },
      row("vi09b") match {
        case "1" => "Excusado y alcantarillado"
        case "2" => "Excusado y pozo séptico"
        case "3" => "Excusado y pozo ciego"
        case "4" => "Letrina"
        case _ => "No Utiliza" // Agregar caso para valor vacío
      },
      row("vi10a") match {
        case "1" => "Por tubería dentro de la vivienda"
        case "2" => "Por tubería fuera de la vivienda pero en el lote"
        case "3" => "Por tubería fuera de la vivienda, lote o terreno"
        case "4" => "No recibe agua por tubería sino por otros medios"
      },
      row("vi13") match {
        case "1" => "Contratan el servicio"
        case "2" => "Servicio municipal"
        case "3" => "Botan a la calle, quebrada, río"
        case "4" => "La queman, entierran"
        case "5" => "Otra"
      }
    ))


  val SQL_PATTERN =
    """
      |INSERT INTO viviendas.ServiciosBasicos (tipoServicioHigienico, alternativaServicioHigienico, servicioDucha, tipoAlumbrado, contieneMedidorAgua, aguaJunta, fuenteAgua, sanitarioPrincipal, aguaRecibida, eliminacionBasura)
      |VALUES
      |("%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s");
      |""".stripMargin

  val scriptData = viviendaData
    .map(vivienda => SQL_PATTERN.formatLocal(java.util.Locale.US,
      escapeMysql(vivienda.tipoServicioHigienico),
      escapeMysql(vivienda.alternativaServicioHigienico),
      escapeMysql(vivienda.servicioDucha),
      escapeMysql(vivienda.tipoAlumbrado),
      escapeMysql(vivienda.contieneMedidorAgua),
      escapeMysql(vivienda.aguaJunta),
      escapeMysql(vivienda.fuenteAgua),
      escapeMysql(vivienda.sanitarioPrincipal),
      escapeMysql(vivienda.aguaRecibida),
      escapeMysql(vivienda.eliminacionBasura)))

  val ScriptFile = new File("C:\\Users\\CM\\Desktop\\serviciosBasicos_insert.sql")
  if (ScriptFile.exists()) ScriptFile.delete()

  scriptData.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\CM\\Desktop\\serviciosBasicos_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))
}
