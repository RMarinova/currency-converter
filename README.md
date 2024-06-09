Task: currency convertor

Create a CLI application that integrates with Fast Forex and lets the user convert currencies with exchange rates from past dates.

Requirements
• Use Python, Node.js or Kotlin.
• The application must accept a command line argument for the date in format '2024-12-31'.
• The application must be able to process multiple conversions.
• The application must continuously validate all inputs until a correct one is submitted. Мonetary values should be constrained to two decimal places. Currencies must be in ISO 4217 three letter currency code format.
• The application must be case-insensitive.
• The application must cache the exchange rates for each requested base currency. Subsequent conversions with this base currency should use the cached data, instead of calling the API.
• Each successful conversion must be saved in a json file with the provided format.
• The application must be terminated by typing 'END' on any input.
• The application must load the api_key for Fast Forex from a config.json file which must be ignored by the version control.
• The executable must be named CurrencyConversion.

For the purposes of this exercise consider that the API calls will never fail.

Example
The application must accept input for Amount, Base currency and Target currency in the given order.
• Input in the example is marked as such
‍• DO NOT include the labels for the input (e.g. ‘Amount’, ‘Base currency', etc).
• Output in the example is BOLD.
//Starts the app with the command line argument
python3 CurrencyConversion.py 2024-05-13

//Amount
10.123
Please enter a valid amount

//Amount
10.23
//Base currency
bng
Please enter a valid currency code

//Base currency
bgn
//Target currency
ERO
Please enter a valid currency code

//Target currency
EUR

10.23 BGN is 5.23 EUR

//Ends the app
end
Example format for conversions.json file:
[ 
    {
        "date": "2024-05-22"
        "amount": 10.23,
        "base_currency": "BGN", 
        "target_currency": "EUR", 
        "converted_amount": 5.23 
    }
]
