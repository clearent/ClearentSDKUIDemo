# Clearent SDK UI Kotlin

## Table of contents

<!-- TOC -->

* [Overview](#sdk-ui-overview)
    * [Clearent SDK UI](#clearent-sdk-ui)
    * [Clearent SDK UI - Options](#clearent-sdk-ui---options)
* [Dependencies](#dependencies---todo)
* [Supported Android versions](#sdk-ui-supported-android-versions)
* [How to integrate](#how-to-integrate)
    * [Note](#note)
    * [Important](#important)
    * [Tips](#tips)
    * [Empower fee](#tips)
    * [Getting started](#getting-started)
        * [Signature](#signature)
        * [Pairing process](#pairing-process)
        * [Transaction](#transaction)
        * [Readers list & reader details](#readers-list--reader-details)
        * [Settings](#settings)
* [Offline mode inside the SDK UI](#offline-mode-inside-the-sdk-ui)
    * [Processing offline transactions](#processing-offline-transactions)
    * [Encountering errors](#encountering-errors)
* [Reader status](#reader-status)
* [Customizing the SDK experience](#customizing-the-sdk-experience)
    * [Colors](#colors)
    * [Fonts](#fonts)
    * [Texts](#texts)
* [Code example](#code-example)

<!-- END -->

* **Additional links**
    * [Kotlin Clearent SDK UI Example](https://github.com/clearent/ClearentSDKUIDemo/tree/Kotlin)
    * [Kotlin Clearent Wrapper Example](https://github.com/clearent/ClearentWrapperDemo/tree/Kotlin)
    * [Java Clearent SDK UI Example](https://github.com/clearent/ClearentSDKUIDemo/tree/Java)
    * [Java Clearent Wrapper Example](https://github.com/clearent/ClearentWrapperDemo/tree/Java)

## SDK UI Overview

Clearent SDK UI is a wrapper over ClearentFrameworkSDK that provides payment capabilities using the
IDTech Android framework to read credit card data using VP3300. Its goal is to ease integration by
providing complete UI that handles all important flows end-to-end.

**Note.** The SDK UI is only available in portrait mode for phones and landscape only for tablets,
with the exception of the signature modal.

### Clearent SDK UI

Wraps all major features of the ClearentFrameworkSDK and adds UI for all major flows:

1. **Pairing Flow**, guides the user through the pairing process steps, taking care of edge cases
   and possible errors.
2. **Transaction Flow**, guides the user through the transaction flow, handling also device pairing
   if needed, takes care of edge cases and error handling.
3. **Readers List & Reader Details**, this flow provides reader management capabilities, it displays
   the status of the current paired reader, but also a list of recently used readers from where you
   can navigate to a settings screen of the reader.
4. **Settings**, allows you to enable optional/disable optional features and manage the offline
   mode.

### Clearent SDK UI - Options

1. **Tips**, when this feature is enabled the first step in the Transaction Flow will be the tips
   screen where the user/client is prompted with UI that will offer some options to choose a tip.
   This feature can be enabled or disabled from your merchant account.
2. **Empower Fee**, this feature is enabled by the terminal settings of the api key/merchant home
   credentials used. When enabled, a modal showing the information of the fee will be shown after
   the tips, if enabled, and before providing the payment method.
3. **Signature**, when this feature is enabled as a last step in the Transaction Flow the SDK will
   display a screen where the user/client can provide a signature. This signature will be uploaded
   to the Clearent backend.
4. **Email receipt**, when this feature is enabled, after the payment is successful and the
   signature has been processed, if enabled, a modal asking if you want to receive an email receipt
   of the payment will be shown. Agreeing and providing a valid email address will sent an email at
   the aforementioned address with the receipt of the transaction.
5. **UI Customization**, Clearent SDK UI provides the integrator the chance to customize the fonts,
   colors and texts used in the UI, This is achieved by overwriting the public properties of each UI
   element that is exposed.
6. **Enhanced Messages**, when this feature is enabled the SDK will provide user friendly messages.

## Dependencies - TODO

## SDK UI Supported Android versions

The SDK supports versions of Android starting from api level 29 (Android 10). Currently supporting
versions 29 (Android 10) throughout 33 (Android 13). With unofficial support for versions 21
(Android 5.0) throughout 28 (Android 10).

## How to Integrate

In order to integrate the **SDK UI** you will need the **API URL**, **API KEY** and the **PUBLIC
KEY**. Use the ClearentWrapper class to update the SDK with this information like this.

```kotlin
fun ClearentWrapper.initializeSDK(
    context: Context, // where context is the ApplicationContext
    baseUrl: String,
    publicKey: String,
    apiKey: String?,
    offlineModeConfig: OfflineModeConfig? = null,
    enhancedMessages: Boolean = true // by default is set to true, and should stay this way because ClearentSDKActivity does not support legacy messaging.
)
```

We recommend calling this method as soon as possible, preferably inside the Application class of
your project.

### Note

The Clearent Wrapper accepts 2 types of credentials:

* apiKey
* MerchantHomeCredentials, which consists of the Id of the selected merchant and the Token inside
  the questJwt of selected terminal, can be set through the field with the same name.

Although both an apiKey and a MerchantHomeCredentials can be provided, the later takes precedence
when sending transactions to the API. Setting a MerchantHomeCredentials will ignore the apiKey.

### Important!

**The safe keeping of the **API URL**, **API KEY/MerchantHomeCredentials** and the
**PUBLIC KEY** is the integrators responsibility. The SDK stores this information only in memory!**

### Getting started

As the name implies, ClearentSDKActivity is an Activity thus it will have to be called through
Android's activity launcher:

```kotlin
val activityLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) {
    // Code to check the result of the activity 
}
val intent = Intent(
    requireContext(),
    ClearentSDKActivity::class.java
) // Use this intent to start the activity
activityLauncher.launch(intent)
```

Inside the intent you will be able to pass in the options.

Now you are ready to use the SDK UI. In order to display the UI from the SDK you need to pass in
what flow you want to be using. If you want to start an action flow you need to set up the following
option inside the intent you will start the activity with:

#### Pairing process

```kotlin
intent.putExtra(
    ClearentSDKActivity.CLEARENT_ACTION_KEY,
    ClearentSDKActivity.CLEARENT_ACTION_PAIR
)

// If you want to show hints for the pairing process you should also do this:
intent.putExtra(
    ClearentSDKActivity.CLEARENT_SHOW_HINTS,
    true
)
```

#### Transaction

Every time you start a transaction you need to pass the amount as a Double to the intent. The SDK UI
provides the option to enter the card details manually or by using the card reader.

**Side note:** if the merchant is enrolled in the Empower quest, a modal with the respective empower
fee will be shown before being prompted for the card/ card data.

```kotlin
intent.putExtra(
    ClearentSDKActivity.CLEARENT_PAYMENT_METHOD,
    PaymentMethod as Parcelable // Where PaymentMethod is an enum inside "com.clearent.idtech.android.wrapper.ui.PaymentMethod" with the values "CARD_READER" and "MANUAL_ENTRY"
)
```

```kotlin
intent.putExtra(
    ClearentSDKActivity.CLEARENT_ACTION_KEY,
    ClearentSDKActivity.CLEARENT_ACTION_TRANSACTION
)
intent.putExtra(
    ClearentSDKActivity.CLEARENT_AMOUNT_KEY,
    amount  // The amount of the transaction which is a Double
)
```

#### Tips

This feature can be enabled from your merchant account. When it's enabled the first step in the
transaction flow will be a prompt where the user/client is prompted with UI that will offer some
options to choose a tip. The options the user/client has are three fixed options in percents and a
custom tip input field. The three options are customizable by calling the *setTipPercentages*
method.

```kotlin
val clearentWrapper = ClearentWrapper.getInstance()
clearentWrapper.setTipPercentages(5, 15, 20)
```

#### Empower fee

This feature can be enabled from your merchant account. When it's enabled a modal containing
information about the fee will be shown after the tips, if enabled, and before being asked to
provide the card/card details. After seeing the fee the user can either cancel or continue the
transaction.

#### Signature

This feature is enabled by passing a boolean to the activity through the intent. If enabled, a modal
will be shown after the transaction is processed successfully asking for the signature. A canvas
will be displayed

The signature feature is enabled by default, if you want to disable it you will have to pass the
option into the aforementioned intent before launching the activity:

```kotlin
intent.putExtra(ClearentSDKActivity.CLEARENT_SHOW_SIGNATURE, false)
```

#### Readers list & reader details

Readers list is a modal showing a list of previously paired readers, with a green dot in front of
the currently paired reader. Next to each reader is an icon that will open the reader's details.
Bellow is a button that will search for all available readers to connect to, that will start the
pairing process.

```kotlin
intent.putExtra(
    ClearentSDKActivity.CLEARENT_ACTION_KEY,
    ClearentSDKActivity.CLEARENT_ACTION_DEVICES
)
```

#### Settings

The settings modal will display at the top the current reader, if any, otherwise will show
"readers". Tapping on it will take you to the readers list. Below are the toggles regarding offline
mode, if the integrator has decided to enable the offline mode, and the button to ask for email
receipts after each transaction

```kotlin
intent.putExtra(
    ClearentSDKActivity.CLEARENT_ACTION_KEY,
    ClearentSDKActivity.CLEARENT_ACTION_SETTINGS
)
```

## Offline mode inside the SDK UI

Inside the *settings* modal there are 2 toggles for the offline mode. The first one enables offline
mode, which means that whenever a transaction is made, regardless of the internet connection or
absence of, it will be stored inside the sdk. The second toggle will ask the user whenever they try
to make a transaction without internet connection, whether they want to enter offline mode or not.

If you want to be informed when the app goes into offline mode there is an interface that will
notify you:

```kotlin
interface OfflineStatusListener {
    fun onOfflineModeChanged(offlineStatus: OfflineStatus)

    sealed class OfflineStatus {
        object Disabled : OfflineStatus()
        data class Enabled(val unprocessedTransactionsSize: Int) : OfflineStatus()
    }
}
```

To receive notifications you can implement the interface and subscribe using the
**ClearentWrapper**

```kotlin
fun addOfflineStatusListener(listener: OfflineStatusListener)
fun removeOfflineStatusListener(listener: OfflineStatusListener)
```

### Processing offline transactions

Once offline transactions are stored in the settings modal a count of the offline transactions will
be shown with the option to "process" them. Pressing on the button will send the transactions to the
API. When the processing is done a button containing the report will appear in place of the process
button that will open the report with the result of the transactions. You must clear the report and
proceed in order to process future offline transactions.

### Encountering errors

If any errors arise, a red text next to the report button will inform you that some, or all,
transactions have failed. Now, because we encountered errors, the error log must be saved in order
to process the next batch of offline transactions. The error log will be saved in the downloads
folder with a name of the following structure: *Offline Transactions Error
Log_yyyy-mm-dd_hh_mm_ss.pdf*.

## Reader status

If you want to display the reader's status in your app you can implement the interface

```kotlin
interface ReaderStatusListener {
    fun onReaderStatusUpdate(readerStatus: ReaderStatus?)
}
```

and then use the 2 methods inside the ClearentWrapper to register for notifications and unregister:

```kotlin
fun addReaderStatusListener(listener: ReaderStatusListener)
fun removeReaderStatusListener(listener: ReaderStatusListener)
```

The interface contains a method that will be called to notify you of the new ReaderStatus which
contains reader related information.

```kotlin
fun onReaderStatusUpdate(readerStatus: ReaderStatus?)
```

How to use it:

```kotlin
// First we create the object that implements the interface
val readerStatusListener = object : ReaderStatusListener {
    override fun onReaderStatusUpdate(readerStatus: ReaderStatus?) {
        // Update your UI
    }
}

// And then we register for the callback inside the OnViewCreated
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    // your code...
    val clearentWrapper = ClearentWrapper.getInstance()
    clearentWrapper.addReaderStatusListener(readerStatusListener)
    // your code...
}

// When we don't use it anymore we unregister from the callback
override fun onDestroyView() {
    // your code...
    val clearentWrapper = ClearentWrapper.getInstance()
    clearentWrapper.removeReaderStatusListener(readerStatusListener)
    // your code...
}
```

If you want to refresh the ReaderStatus you can always call the following method:

```kotlin
val clearentWrapper = ClearentWrapper.getInstance()
clearentWrapper.startDeviceInfoUpdate()
```

## Customizing the SDK experience

The SDK provides the option to customize the fonts, colors and texts used in the SDK. This can be
achieved by overriding certain attributes in styles, strings and colors xml files. Check
our [Example](https://github.com/clearent/ClearentSDKUIDemo/tree/Kotlin) for full customization.

### Colors

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="color_primary">#FFFF00</color>
    <color name="color_secondary">#00FFFF</color>
</resources>
```

### Fonts

You will need to override the fontFamily attribute of the styles "FontFamilyRegular" and "
FontFamilyBold".

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="FontFamilyRegular" parent="TextAppearance.AppCompat">
        <item name="fontFamily">@font/sf_pro_text_regular</item>
    </style>

    <style name="FontFamilyBold" parent="TextAppearance.AppCompat">
        <item name="fontFamily">@font/sf_pro_text_bold</item>
    </style>
</resources>
```

### Texts

In order to customize texts used in the SDK you will need to override strings inside the strings.xml
with the corresponding names.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="tips_title">Would you like to add a tip?</string>
    <string name="tips_confirmation_button_text">Charge $%1$s</string>
    <string name="tips_skip_button_text">Maybe next time</string>
    <string name="tips_percentage_label">"%1$d%% ($%2$s)"</string>
</resources>
```

## Code example

[Kotlin EXAMPLE](https://github.com/clearent/ClearentSDKUIDemo/tree/Kotlin) of the
ClearentSDKActivity integration.

```kotlin
class App : Application() {

    private val clearentWrapper = ClearentWrapper.getInstance()

    override fun onCreate() {
        super.onCreate()

        initClearentWrapper()
    }

    private fun initClearentWrapper() = clearentWrapper.initializeSDK(
        context,
        baseUrl,
        publicKey,
        apiKey,
        offlineModeConfig,
        enhancedMessages // by default is set to true and can be omitted
    )
}
```

```kotlin
class MainActivity : AppCompatActivity() {

    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    private val clearentWrapper = ClearentWrapper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting up the views.
        // ...

        // This method will setup the ClearentDataSource object to send data over to the 
        // ClearentSDKActivity. Although we do not recommend, you can also implement your 
        // custom *ClearentWrapperListener*.
        clearentWrapper.setListener(ClearentDataSource)
    }

    private fun startSdkUiActivity() {
        val intent = Intent(applicationContext, ClearentSDKActivity::class.java)
        val clearentAction = ClearentAction.Pairing() // Choose the action you want to take
        clearentAction.prepareIntent(intent) // Helper function to add extras to the intent.
        // You can also do this manually but the sdk takes care
        // of that for you.
        activityLauncher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearentWrapper.removeListener()
    }
}
```
