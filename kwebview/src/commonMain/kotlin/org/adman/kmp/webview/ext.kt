package org.adman.kmp.webview

enum class TextAlign(val cssValue: String) {
    LEFT("left"),
    RIGHT("right"),
    CENTER("center"),
    JUSTIFY("justify")
}

fun String.formatHtmlContent(
    fontSize: Int = 12,
    textAlign: TextAlign = TextAlign.JUSTIFY,
    fontColor: String = "#000000"
): String {

    return "<!doctype html>\n" +
            "<html>\n" +
            "\n" +
            "<head>\n" +
            "<style type=\"text/css\">\n" +
            "body {\n" +
            "    font-size: ${fontSize}px;\n" +
            "    text-align: ${textAlign.cssValue};\n" +
            "    color:${fontColor};\n" +
            "}\n" +
            "</style>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html;  charset=utf-8\">\n" +
            "</head>\n" +
            "\n" +
            "<body dir=\"rtl\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "<style>img{display: inline; height: auto; width: 300px; max-width: 100%;align:top}</style>" +
            "\n" +
            this +
            "\n" +
            "</body>\n" +
            "\n" +
            "</html>"
}