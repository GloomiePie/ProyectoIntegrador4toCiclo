package ProyectoIntegrador

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}
object GeneracionScriptsConglomerado extends App{

  val reader = CSVReader.open(new File("C:\\Users\\USUARIO\\Downloads\\parroquias_nuevo.csv"))(new DefaultCSVFormat {
    override val delimiter = ';'
  })

  val data = reader.allWithHeaders()
  reader.close()

  val ciudades = data
    .map(row => (row("Codigo"),
      row("Canton").replaceAll("Ã‘", "Ñ")))


  val ciudadesSec = List(
    ("Quito", "Urbano"),
    ("Guayaquil", "Urbano"),
    ("Cuenca", "Urbano"),
    ("Loja", "Urbano"),
    ("Manta", "Urbano"),
    ("Ambato", "Urbano"),
    ("Esmeraldas", "Urbano"),
    ("Riobamba", "Urbano"),
    ("Ibarra", "Urbano"),
    ("Portoviejo", "Urbano"),
    ("Santo Domingo", "Urbano"),
    ("Machala", "Urbano"),
    ("Durán", "Urbano"),
    ("Quevedo", "Urbano"),
    ("Milagro", "Urbano"),
    ("Santo Domingo de los Colorados", "Urbano"),
    ("Latacunga", "Urbano"),
    ("Pelileo", "Rural"),
    ("Zamora", "Rural"),
    ("La Libertad", "Rural"),
    ("Santa Elena", "Rural"),
    ("Azogues", "Rural"),
    ("Tena", "Rural"),
    ("Babahoyo", "Rural"),
    ("Machachi", "Rural"),
    ("Otavalo", "Rural"),
    ("Puyo", "Rural"),
    ("Chone", "Rural")
  )

  case class Ciudad(
                   idCiudad: Int,
                   nombreCiudad: String,
                   Canton_idCanton: Int,
                   idSector: Int
                   )

  val ciudadesPorSector = ciudadesSec.map { case (ciudad, sector) =>
    (ciudad, if (sector == "Urbano") 1 else 2)
  }
  val union = ciudades.flatMap(ciudad => ciudadesPorSector.find(_._1 == ciudad._2)
    .map(sector => (ciudad._1, ciudad._2, ciudad._1.reverse.substring(2, 4).reverse, sector._2)))

  println(ciudades)
  println(ciudadesPorSector)

  val unionData = union
    .map(row => Ciudad(
      row._1.toInt,
      row._2,
      row._3.toInt,
      row._4
    ))

  val SQL_PATTERN =
    """
      |INSERT INTO viviendas.ciudad (idCiudad, nombreCiudad, Canton_idCanton, Sector_idSector)
      |VALUES
      |(%d,"%s", %d, %d);
      |""".stripMargin


  val scriptData = unionData
    .map(ciudad => SQL_PATTERN.formatLocal(java.util.Locale.US,
      ciudad.idCiudad,
      ciudad.nombreCiudad,
      ciudad.Canton_idCanton,
      ciudad.idSector))

  val ScriptFile = new File("C:\\Users\\USUARIO\\Desktop\\Ciudad_insert.sql")
  if (ScriptFile.exists()) ScriptFile.delete()

  scriptData.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\Ciudad_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))


}
