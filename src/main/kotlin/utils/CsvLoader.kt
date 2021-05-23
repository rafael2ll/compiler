package utils

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File


class CsvLoader() {
    val csvMapper: CsvMapper

    init {
        csvMapper = CsvMapper().apply {
            registerModule(KotlinModule())
        }
        csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY)
    }

    fun readCsvFile(fileName: String): ArrayList<Any> {
        File(this.javaClass.getResource(fileName).toURI()).let { reader ->
            return csvMapper
                .readerFor(List::class.java)
                .readValues<Any>(reader)
                .readAll()
                .toList() as ArrayList<Any>
        }
    }
}
