package com.vanjor.fragmentincomposehelper.util

object XMLUtil {
    fun xmlFileNameFromBinding(bindingClassName: String): String {
        val layoutName = bindingClassName
            .replace(Regex("Binding$"), "")
            .camelToSnake()

        return "$layoutName.xml"
    }

    fun generateFragmentContainerXml(id: String, fragmentClass: String): String {
        return """
            <androidx.fragment.app.FragmentContainerView
                xmlns:android="http://schemas.android.com/apk/res/android"
                android:id="@+id/${id}"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:name="$fragmentClass" />
        """.trimIndent()
    }

    fun generateAndroidViewBindingSnippet(fragmentClass: String, xmlFileName: String, viewId: String): String {
        val variableName = fragmentClass.replaceFirstChar { it.lowercase() }
        val viewIdCamel = viewId.snakeToCamel()
        val bindingName = xmlFileName.split('.').first().snakeToCamel().replaceFirstChar { it.uppercase() }
        return """
        AndroidViewBinding(${bindingName}Binding::inflate) {
            val $variableName = $viewIdCamel.getFragment<$fragmentClass>()
            // ...
        }
        """.trimIndent()
    }

    fun String.snakeToCamel(): String {
        val pattern = "_[a-z]".toRegex()
        return replace(pattern) { it.value.last().uppercase() }
    }

    fun String.camelToSnake(): String {
        val pattern = "(?<=.)[A-Z]".toRegex()
        return this.replace(pattern, "_$0").lowercase()
    }
}