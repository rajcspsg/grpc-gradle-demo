package com.raj.grpc.server.dao;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountDao {

    private static final Map<Integer, Integer> accounts = IntStream.rangeClosed(1, 10)
            .boxed()
            .collect(Collectors.toMap(x -> x, v -> 100 ));

    public static  int getBalance(int accountId){
        return accounts.get(accountId);
    }

    public static Integer addBalance(int accountId, int amount) {
        return accounts.computeIfPresent(accountId, (k, v) -> v + amount);
    }

    public static Integer deductBalance(int accountId, int amount) {
        return accounts.computeIfPresent(accountId, (k,v) -> v - amount);
    }

    public static void printAccountDetails() {
        System.out.println(accounts);
    }
}