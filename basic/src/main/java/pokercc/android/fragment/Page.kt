package pokercc.android.fragmentnavigator

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Page(
    val title: String,
    val url: String
) {
    @Target(AnnotationTarget.PROPERTY)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Arg(
        val name: String
    )
}