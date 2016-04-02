# AppDelay (by DGApps)
An app to delay app launches for Android and Android Wear

###How it works:
App Delay uses The PackageManager built into android to get a list of apps. Then the Packagename,app label and app icon are gotten from the chosen app's application info, also using PackageManger. Then JodaTime is used for when the user selects 'date', or the a custom formula is used to calculate the total amount of time to wait in milliseconds if the user selects 'countdown'. Finally a Service is launched with all that information, that service uses either AlarmManager, for waiting until the specified date, or CountDownTimer, if the delay time is a countdown.

<a href="https://play.google.com/store/apps/details?id=com.daniel.apps.appdelay&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" /></a>
