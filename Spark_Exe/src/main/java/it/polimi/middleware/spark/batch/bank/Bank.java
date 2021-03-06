package it.polimi.middleware.spark.batch.bank;

import static org.apache.spark.sql.functions.max;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import it.polimi.middleware.spark.utils.LogUtils;

import javax.xml.crypto.Data;

/**
 * Bank example
 *
 * Input: csv files with list of deposits and withdrawals, having the following
 * schema ("person: String, account: String, amount: Int)
 *
 * Queries
 * Q1. Print the total amount of withdrawals for each person.
 * Q2. Print the person with the maximum total amount of withdrawals
 * Q3. Print all the accounts with a negative balance
 */
public class Bank {
    private static final boolean useCache = true;

    public static void main(String[] args) {
        LogUtils.setLogLevel();

        final String master = args.length > 0 ? args[0] : "local[4]";
        final String filePath = args.length > 1 ? args[1] : "./";
        final String appName = useCache ? "BankWithCache" : "BankNoCache";

        final SparkSession spark = SparkSession
                .builder()
                .master(master)
                .appName("Bank")
                .getOrCreate();

        final List<StructField> mySchemaFields = new ArrayList<>();
        mySchemaFields.add(DataTypes.createStructField("person", DataTypes.StringType, true));
        mySchemaFields.add(DataTypes.createStructField("account", DataTypes.StringType, true));
        mySchemaFields.add(DataTypes.createStructField("amount", DataTypes.IntegerType, true));
        final StructType mySchema = DataTypes.createStructType(mySchemaFields);

        final Dataset<Row> deposits = spark
                .read()
                .option("header", "false")
                .option("delimiter", ",")
                .schema(mySchema)
                .csv(filePath + "files/bank/deposits.csv");

        final Dataset<Row> withdrawals = spark
                .read()
                .option("header", "false")
                .option("delimiter", ",")
                .schema(mySchema)
                .csv(filePath + "files/bank/withdrawals.csv");

        // Q1. Total amount of withdrawals for each person

        Dataset<Row> sumWithdrowal = withdrawals.groupBy("person").sum("amount");
        sumWithdrowal.show();

        // Q2. Person with the maximum total amount of withdrawals

        long maxWithdrowal = sumWithdrowal.agg(max("sum(amount)")).first().getLong(0);
        Dataset<Row> maxPerson = sumWithdrowal.filter(sumWithdrowal.col("sum(amount)").equalTo(maxWithdrowal));
        maxPerson.show();

        // Q3 Accounts with negative balance

        Dataset<Row> totDeposit = deposits.groupBy("account").sum("amount");
        Dataset<Row> totWithdrowal = withdrawals.groupBy("account").sum("amount");

        Dataset<Row> negativeAccounts = totWithdrowal.join(totDeposit, totDeposit.col("account").equalTo(totWithdrowal.col("account")), "left_outer")
                .filter(totWithdrowal.col("sum(amount)").gt(totDeposit.col("sum(amount)"))
                    .or(totDeposit.col("sum(amount)").isNull().and(totWithdrowal.col("sum(amount)").gt(0)))
                ).select(totWithdrowal.col("account"));

        //totDeposit.show();
        //totWithdrowal.show();
        negativeAccounts.show();

        spark.close();

    }
}