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
## 位元運算

#### (1)保留正負號位元左右旋(<<, >>)
例1:正數

    int n = 10;
    n = n >> 1;
    System.out.println(n); // 5

1. 10的二進制為

        00000000 00000000 00000000 00001010

2. `n = n >> 1;` 代表向右旋一次，即為

        00000000 00000000 00000000 00000101

例2:負數

    int n = -10;
    n = n >> 1;
    System.out.println(n); // -5

1. -10的二進制為

        11111111 11111111 11111111 11110110

2. `n = n >> 1;` 代表向右旋一次，即為

        11111111 11111111 11111111 11111011

#### (2)不保留正負號位元左右旋(<<<, >>>)
例1:正數

    int n = 10;
    n = n >>> 1;
    System.out.println(n); // 5

1. 10的二進制為

        00000000 00000000 00000000 00001010

2. `n = n >>> 1;` 代表**不保留正負位元**向右旋一次，即為

        00000000 00000000 00000000 00000101

例2:負數

    int n = -10;
    n = n >>> 1;
    System.out.println(n); // 2147483643

1. -10的二進制為

        11111111 11111111 11111111 11110110

2. `n = n >>> 1;` 代表**不保留正負位元**向右旋一次，即為

        01111111 11111111 11111111 11111011
