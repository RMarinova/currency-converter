import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.DecimalFormat
import java.time.LocalDate

@Serializable
data class Conversion(
    val date: String,
    val amount: Double,
    val base_currency: String,
    val target_currency: String,
    val converted_amount: Double
)

@Serializable
data class ExchangeRateResponse(val base: String, val date: String, val ms: Int, val results: Map<String, Double>)

@Serializable
data class CurrenciesResponse(val currencies: Map<String, String>, val ms: Int)

val apiKey = Json.decodeFromString<Map<String, String>>(File("src/main/resources/config.json").readText())["api_key"]
val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}
val cache = mutableMapOf<String, ExchangeRateResponse>()
val conversions = mutableListOf<Conversion>()
val currencies = mutableMapOf<String, String>()

suspend fun getExchangeRates(date: String, baseCurrency: String): ExchangeRateResponse {
    cache[baseCurrency]?.let {
        return it
    }
    val response: HttpResponse = client.get("https://api.fastforex.io/historical") {
        parameter("date", date)
        parameter("from", baseCurrency)
        parameter("api_key", apiKey)
    }
    val exchangeRateResponse: ExchangeRateResponse = response.body()
    cache[baseCurrency] = exchangeRateResponse
    return exchangeRateResponse
}

suspend fun getCurrencies(): Map<String, String> {
    if (currencies.isEmpty()) {
        val response: HttpResponse = client.get("https://api.fastforex.io/currencies") {
            parameter("api_key", apiKey)
        }
        val currenciesResponse: CurrenciesResponse = response.body()
        currencies.putAll(currenciesResponse.currencies)
    }

    return currencies
}

suspend fun isValidCurrencyCode(code: String): Boolean {
    return code.length == 3 && code.all { it.isLetter() } && getCurrencies()[code] != null
}

fun isValidAmount(amount: String): Boolean {
    if (!amount.contains('.') || amount.indexOf('.') != amount.length - 3) {
        return false
    }
    return try {
        amount.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun main(args: Array<String>) = runBlocking {
    if (args.isEmpty()) {
        println("Please provide a date in the format 'YYYY-MM-DD'.")
        return@runBlocking
    }

    val date = args[0]
    try {
        LocalDate.parse(date)
    } catch (e: Exception) {
        println("Please provide a valid date in the format 'YYYY-MM-DD'.")
        return@runBlocking
    }

    val decimalFormat = DecimalFormat("#.##")
    mainLoop@
    while (true) {

        var amount: Double
        var baseCurrency: String
        var targetCurrency: String

        while (true) {
            print("Amount: ")
            val amountInput = readLine()!!.trim().lowercase()
            if (amountInput == "end") break@mainLoop
            if (!isValidAmount(amountInput)) {
                println("Please enter a valid amount.")
                continue
            }
            amount = decimalFormat.format(amountInput.toDouble()).toDouble()
            break
        }

        while (true) {
            print("Base currency: ")
            baseCurrency = readLine()!!.trim().uppercase()
            if (baseCurrency == "END") break@mainLoop
            if (!isValidCurrencyCode(baseCurrency)) {
                println("Please enter a valid currency code.")
                continue
            }
            break
        }

        while (true) {
            print("Target currency: ")
            targetCurrency = readLine()!!.trim().uppercase()
            if (targetCurrency == "END") break@mainLoop
            if (!isValidCurrencyCode(targetCurrency)) {
                println("Please enter a valid currency code.")
                continue
            }
            break
        }

        val exchangeRateResponse = getExchangeRates(date, baseCurrency)

        val conversionRate = exchangeRateResponse.results[targetCurrency]
        if (conversionRate == null) {
            println("Conversion rate for $targetCurrency not found.")
            continue
        }

        val convertedAmount = decimalFormat.format(amount * conversionRate).toDouble()
        println("$amount $baseCurrency is $convertedAmount $targetCurrency")

        val conversion = Conversion(date, amount, baseCurrency, targetCurrency, convertedAmount)
        conversions.add(conversion)
        
        val json = Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }

        val jsonString = json.encodeToString(conversions)

        File("conversions.json").writeText(jsonString)
    }

    client.close()
}
