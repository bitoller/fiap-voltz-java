package br.com.voltz.model;

import java.util.Arrays;
import java.util.Optional;

public enum SupportedCrypto {

    BITCOIN("Bitcoin", "BTC"),
    ETHEREUM("Ethereum", "ETH"),
    RIPPLE("Ripple", "XRP"),
    CARDANO("Cardano", "ADA"),
    SOLANA("Solana", "SOL"),
    POLKADOT("Polkadot", "DOT"),
    DOGECOIN("Dogecoin", "DOGE"),
    LITECOIN("Litecoin", "LTC"),
    CHAINLINK("Chainlink", "LINK"),
    BITCOIN_CASH("Bitcoin Cash", "BCH"),
    TRON("TRON", "TRX"),
    AVALANCHE("Avalanche", "AVAX"),
    POLYGON("Polygon", "MATIC"),
    STELLAR("Stellar", "XLM"),
    MONERO("Monero", "XMR"),
    ETHEREUM_CLASSIC("Ethereum Classic", "ETC"),
    VECHAIN("VeChain", "VET"),
    COSMOS("Cosmos", "ATOM"),
    UNISWAP("Uniswap", "UNI"),
    ALGORAND("Algorand", "ALGO");

    private final String name;
    private final String symbol;

    SupportedCrypto(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Optional<SupportedCrypto> fromSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return Optional.empty();
        }

        String upperSymbol = symbol.toUpperCase();
        return Arrays.stream(values())
                .filter(crypto -> crypto.symbol.equals(upperSymbol))
                .findFirst();
    }

    public static String[] getAllSymbols() {
        return Arrays.stream(values()).map(SupportedCrypto::getSymbol).toArray(String[]::new);
    }
}