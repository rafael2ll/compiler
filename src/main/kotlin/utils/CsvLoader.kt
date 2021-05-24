package utils

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.charset.Charset


class CsvLoader

fun readCsvFile(fileName: String): ArrayList<ArrayList<String>> {
    val csvMapper = CsvMapper().apply {
        registerModule(KotlinModule())
    }
    csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY)
    CsvLoader::class.java.getResource(fileName)!!.readText(Charset.defaultCharset()).let { reader ->
        return csvMapper
            .readerFor(List::class.java)
            .readValues<Any>(reader)
            .readAll()
            .toList() as ArrayList<ArrayList<String>>
    }
}
