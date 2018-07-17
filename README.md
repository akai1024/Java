# Java
蒐集些JAVA的問題解決


String的使用
1. .split()方法時會使用正則表達式判斷，因此"|"必須加入跳脫字元為"\\|"，詳閱正則表達式字符(|, [, ] ...)
2. .replaceAll()也會使用正則表達式




Gson的使用
1. 避免產生unicode字元的建構方法Gson gson = new GsonBuilder().disableHtmlEscaping().create();
