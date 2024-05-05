# Fragment In Compose Helper
This is an Android Studio plugin designed to streamline the process of using Fragment in Jetpack Compose. 

It automatically generates XML file for the fragment with pre-filled fields, and can add Compose code snippets to your clipboard.

## Demo
![demo](https://github.com/WenjiuWang/FragmentInComposeHelper/assets/56171936/d5295fb9-58df-4f44-b94d-71d1b13e2d4b)

## Requirement
The plugin is based on the official [interoperbility document](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/views-in-compose#fragments-in-compose), which requires the projet to 
- enable [view binding](https://developer.android.com/topic/libraries/view-binding#setup)
- have dependency `androidx.compose.ui:ui-viewbinding`

## Usage
- Install this plugin from either the market place, or local file(Plugins->Install Plugin from Disk)
- Right click on a Fragment file
- Select "Generate FragmentContainerView`
- Review and edit the generated XML file name and view ID, then click OK
- If file is generated succesfully, Android Studio will automatically navigate to it
- Click on the "Copy AndroidViewBinding Snippet" to have the Compose code snippet copied to your clipboard
