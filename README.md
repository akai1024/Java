# Java
蒐集些JAVA的問題解決


## String的使用
1. `.split()`方法時會使用**正則表達式**判斷，因此"|"必須加入跳脫字元為"\\|"，詳閱正則表達式字符(|, [, ] ...)
2. `.replaceAll()`也會使用正則表達式

----------


## Gson的使用
1. 避免產生unicode字元的建構方法

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

----------

## HTTP request
1. 編碼以UTF-8的byte[]寫入時，先把字串用`.getBytes("UTF-8")`再write，例如:

        outPut.write(json.getBytes("UTF-8"));

----------

## Serializable應用
1. 簡單的序列化成file

        SomeObject object = new SomeObject();
        FileOutputStream fs = new FileOutputStream("some.save");
        ObjectOutputStream os = new ObjectOutputStream(fs);
        os.writeObject(object);
        os.close();

2. 簡單的反序列化

        FileInputStream fs = new FileInputStream("some.save");
        ObjectInputStream os = new ObjectInputStream(fs);
        SomeObject car = (SomeObject) os.readObject();
        os.close();

3. 透過反序列化的物件只有記憶體位置不同，若所有的成員都實現Serializable值將完全保留

----------
