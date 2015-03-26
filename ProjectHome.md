**Experimental/Academic! Not for Regular Usage**

Barred is a file archiver with a pure Java implementation of the BWT algorithm combined with certain additional compression stages and performance optimization to make the data more compressed.

Barred compresses text files much better than other file compressors.

Algorithms used in Barred:

1. RLE+BWT(variable block-qsort)+MTF+RLE+ENT(Huffman). It is a text book implementation of these patented algorithms. Check out the respective algorithm page/technical paper or contact the respective author for information on usage for your own implementation.

2. MD5 Message Digest for storing pass phrases.

The following tables shows how Barred performs with some text artifacts. For some artifacts, bz2 compression performed marginally better because bz2 is also based on the algorithm sequence used by Barred (BWT+MTF+Huffman). However, Barred is a file archiver and can store more files into a single bar file.



### The Three Musketeers ###
http://onlinebooks.library.upenn.edu/webbin/gutbook/lookup?num=1257

| **Formats** | **Orig. Size (Bytes)** | **Comp. Size(Bytes)** | **Rank** |
|:------------|:-----------------------|:----------------------|:---------|
|zip|1346271|490503| 5 |
|gz|1346271|490416| 4 |
|rar|1346271|417792| 3 |
|bz2|1346271|348815| 1 |
| **bar** | **1346271** | **357508** | **2** |

Barred compressed the text file with a block size of 2MB ( b 2 option) and a compression ratio of 73.44458 % with 2.1242434 bps.

## The Bible ##
http://corpus.canterbury.ac.nz/resources/large.tar.gz

| **Formats** | **Orig. Size (Bytes)** | **Comp. Size(Bytes)** | **Rank** |
|:------------|:-----------------------|:----------------------|:---------|
|zip|4047392|1114005| 4 |
|gz|4047392|1191271| 5 |
|rar|4047392|983040| 3 |
|bz2|4047392|845635| 1 |
| **bar** | **4047392** | **854555** | **2** |

Barred compressed the text file with a block size of 5MB ( b 5 option) and a compression ratio of 78.886284 % with 1.6890975 bps.

## E.coli ##
http://corpus.canterbury.ac.nz/resources/large.tar.gz

| **Formats** | **Orig. Size (Bytes)** | **Comp. Size(Bytes)** | **Rank** |
|:------------|:-----------------------|:----------------------|:---------|
|zip|4638690|1341250| 5 |
|gz|4638690|1238379| 2 |
|rar|4638690|1327104| 4 |
|bz2|4638690|1251004| 3 |
| **bar** | **4638690** | **1207372** | **1** |

Barred compressed the text file with a block size of 5MB ( b 5 option) and a compression ratio of 73.9717 % with 2.0822637 bps.

## Large XML File ##
http://www.ins.cwi.nl/projects/xmark/Assets/standard.gz

| **Formats** | **Orig. Size (Bytes)** | **Comp. Size(Bytes)** | **Rank** |
|:------------|:-----------------------|:----------------------|:---------|
|zip|116524435 (111.1 MB)|36724047 (35.0 MB)| 4 |
|gz|116524435 (111.1 MB)|37924307 (36.2 MB)| 5 |
|rar|116524435 (111.1 MB)|28774400 (27.4 MB)| 3 |
|bz2|116524435 (111.1 MB)|25549826 (24.4 MB)| 2 |
| **bar** | **116524435 (111.1 MB)** | **13105839 (12.4 MB)** | **1** |

Barred compressed the text file with a block size of 20MB ( b 60 option) and a compression ratio of 88.75271 % with **0.8997831** bps.

Barred uses a combination of algorithms like BWT, MTF, RLE, and ENT with different permutations to compress the files for better result. Barred as a file archiver works across multiple platforms with a Java runtime (>=1.6).

[Download the Barred executable](http://barred.googlecode.com/files/barred.jar).

Some examples of usage:

```
//Compressing
java -jar barred.jar -c <input_file> Ouput.bar
java -jar barred.jar -c <input_dir> Ouput.bar

//Extracting
java -jar barred.jar -x Output.bar <output_dir>
```

**Note** You need to have Java 1.6 or higher runtime installed. Otherwise, you will get the Java's 'Unsatisfied Link' error.

### The Protein Corpus ###

Most difficult to compress because of little Markov dependency.

http://www.data-compression.info/Corpora/ProteinCorpus.zip

**hi**

```
  Orig. Size : 509519 Bytes (497.5 KB)
  Comp. Size : 273907 Bytes (267.4 KB)
  Comp. Ratio: 46.242043 %
        bps  : 4.3006363 bps
```

**hs**

```
  Orig. Size : 3295751 Bytes (3.1 MB)
  Comp. Size : 1726804 Bytes (1.6 MB)
  Comp. Ratio: 47.605145 %
        bps  : 4.1915884 bps
```

**mj**

```
  Orig. Size : 448779 Bytes (438.2 KB)
  Comp. Size : 237906 Bytes (232.3 KB)
  Comp. Ratio: 46.988163 %
        bps  : 4.2409472 bps
```

**sc**

```
  Orig. Size : 2900352 Bytes (2.7 MB)
  Comp. Size : 1528937 Bytes (1.4 MB)
  Comp. Ratio: 47.284435 %
        bps  : 4.2172456 bps
```

### Extreme Compression ###

While used Barred, you can manually specify the internal blocks size to override the default block size of 10 MB. Hypothetically, best compression is possible in Barred if the size of the block is same as the size of the file being compressed.

For instance, if you want maximum compression for a file of size 4.5 MB, pass the option **-b 5** so that the internal sorting block is just one. This will make Barred slow but maximum compression is obtained. However, if you are compressing big files like a 100 MB file with -b 100 option, your machine would not be able to take it and you will most likely get a **Out of Memory** error because of the exhausted heap size. You can try increasing the heap size by passing **-Xms512m -Xmx1024m** to the JVM.

```
java -Xms512m -Xmx1024m -jar barred.jar -c <input_file> Ouput.bar -b 20
```

**Note** Higher the block size slower the tool becomes but better is the compression. But duh you don't have a super computer!

For comments, queries, bugs, contact me at fermatjen AT yahoo DOT com