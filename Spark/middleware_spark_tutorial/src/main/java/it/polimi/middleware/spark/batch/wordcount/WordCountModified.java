package it.polimi.middleware.spark.batch.wordcount;

import it.polimi.middleware.spark.utils.LogUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Int;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class WordCountModified {

    public static void main(String[] args) {
        LogUtils.setLogLevel();

        final String master = args.length > 0 ? args[0] : "local[4]";
        final String filePath = args.length > 1 ? args[1] : "./";

        final SparkConf conf = new SparkConf().setMaster(master).setAppName("WordCount");
        final JavaSparkContext sc = new JavaSparkContext(conf);

        final JavaRDD<String> lines = sc.textFile(filePath + "files/wordcount/in.txt");
        final JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());

        // Q1. For each character, compute the number of words starting with that character
        final JavaPairRDD<String, Integer> pairs = words.mapToPair(s -> new Tuple2<>(s.substring(0,1), 1));
        final JavaPairRDD<String, Integer> counts = pairs.reduceByKey((a, b) -> a + b);
        System.out.println(counts.collect());
        // Q2. For each character, compute the number of lines starting with that character
        final JavaPairRDD<String, Integer> pairs2 = lines.mapToPair(s -> new Tuple2<>(s.substring(0,1), 1));
        final JavaPairRDD<String, Integer> counts2 = pairs2.reduceByKey((a, b) -> a + b);
        System.out.println(counts2.collect());

        // Q3. Compute the average number of characters in each line

        final JavaRDD<Integer> pairs3 = lines.map(s -> s.length());
        final double counts3 = (double) pairs3.reduce((a, b) -> a + b) / pairs3.count();
        System.out.println(counts3);

        //In another way
        final Tuple2<Integer, Integer> avgTuple = lines
                .mapToPair(line -> new Tuple2<>(1, line.length()))
                .reduce((s, t) -> new Tuple2<>(s._1 + t._1, s._2 + t._2));

        //lines.foreach(line -> System.out.println(line.length()));

        final double average = (double) avgTuple._2 / avgTuple._1;
        System.out.println(average);

        sc.close();
    }

}