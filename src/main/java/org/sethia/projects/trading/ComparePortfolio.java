package org.sethia.projects.trading;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.sethia.utils.Grapher;
import org.sethia.utils.Tuple;

public class ComparePortfolio {

  private static final DateFormat DF = new SimpleDateFormat("yyyymmdd");

  public static void main(String[] args)
      throws IOException, ArgumentParserException, InterruptedException {

    Namespace ns = parseArgs(args);

    // load existing portfolio
    Portfolio portfolio = Portfolio.Builder.fromYahooFinanceCSV(ns.getString("csv"), "stocks");

    List<Tuple<Double, Double>> appreciationOfPortfolio = new ArrayList<>();
    for(Tuple<Date, Double> t: portfolio.getAppreciationSinceStart()){
      appreciationOfPortfolio.add(Tuple.of(Double.valueOf(DF.format(t.getLeft())), t.getRight()));
    }

    Grapher grapher = new Grapher("Performance of portfolio", "");
    grapher.addLineGraph(appreciationOfPortfolio, portfolio.getName());
    grapher.render();

    TimeUnit.SECONDS.sleep(45);

    /*final Map<String, List<Tuple<Double, Double>>> gainsByTicker = portfolio
        .compareGainsWith(ns.getList("compareTo"));

    Grapher grapher = new Grapher("Comparing portfolio with " + ns.getList("compareTo"), "");
    for (Entry<String, List<Tuple<Double, Double>>> entry : gainsByTicker.entrySet()) {
      grapher.addLineGraph(entry.getValue(), entry.getKey());
    }

    grapher.render();*/
  }

  private static Namespace parseArgs(String[] args) throws ArgumentParserException {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("Portfolio Comparer", true)
        .description("Compare performance of portfolio wrt any ticker(s)");

    parser.addArgument("--csv").required(true)
        .help("path of Yahoo Finance csv file with portfolio data");

    String[] defaultTickers = Arrays.stream(Ticker.values()).map(t -> t.getName())
        .toArray(String[]::new);
    parser.addArgument("--compareTo").nargs("+").setDefault(defaultTickers)
        .help("Tickers of stocks to compare with.");

    return parser.parseArgs(args);
  }
}
