[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

![](poster.jpg)

### What's new in 1.9.4:
- Option to enable javascript content

<!-- GETTING STARTED -->
## Getting Started
### Adding dependencies
- Add it in your `commonMain.dependencies`
  ```
  implementation("io.github.shadmanadman:kmp-webview:1.9.4")
  ```

### Usage  
```
@Composable
fun KmpWebViewScreen(
    modifier: Modifier? = null,
    url: String? = null,
    htmlContent: String? = null,
    enableJavaScript: Boolean = false,
    isLoading: ((isLoading: Boolean) -> Unit)? = null,
    onUrlClicked: ((url: String) -> Unit)? = null
)
```
- `isLoading`: Current loading status of the web view
- `onUrlClicked`: If user click's on a link inside your web view you can handel it here. 
   - *Note*: Images(jpg,pngs) and attachment.id are excluded and will be opened inside your     
     current web view.
   - *Note*: If you don't implement this, all links will be opened in your current web view.
- `enableJavaScript`: Enable javascript content

<h1 align="center">Any contribution is very satisfying. </h1>
<h2 align="center">Make it easier for everyone</h2>
