package org.sethia.projects.trading;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.sethia.utils.graphing.LineGrapherJFreeChart;
import org.sethia.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComparePortfolio {

  private static final Logger log = LoggerFactory.getLogger(ComparePortfolio.class);

  public static void main(String[] args)
      throws IOException, ArgumentParserException, InterruptedException, ParseException {

    Namespace ns = parseArgs(args);

    // load existing portfolio
    Portfolio portfolio = Portfolio.Builder.fromYahooFinanceCSV(ns.getString("csv"), "K's stocks");

    LineGrapherJFreeChart lineGrapher = new LineGrapherJFreeChart("Performance of portfolio",
        "Time", "Gain %age");

    List<Tuple<Date, Double>> appreciationOfPortfolio = portfolio.getAppreciationSinceStart();
    log.debug("Tuples for {}:{}{}", portfolio.getName(), System.lineSeparator(),
        appreciationOfPortfolio.toString());

    appreciationOfPortfolio.forEach(
        t -> lineGrapher.addPoint(t.getLeft(), t.getRight(), portfolio.getName())
    );

    log.debug("comparing portfolio to: " + ns.getList("compareTo").toString());

    for (Object tickerObj : ns.getList("compareTo")) {
      String tickerName = (String) tickerObj;
      Portfolio copiedPortfolio = Portfolio.Builder
          .fromExistingPortfolioWithReplacedTicker(portfolio, tickerName);

      List<Tuple<Date, Double>> appreciationOfCopiedPortfolio = copiedPortfolio
          .getAppreciationSinceStart();
      log.trace("Tuples for {}:{}{}", copiedPortfolio.getName(), System.lineSeparator(),
          appreciationOfCopiedPortfolio.toString());
      appreciationOfCopiedPortfolio
          .forEach(t -> lineGrapher.addPoint(t.getLeft(), t.getRight(), copiedPortfolio.getName()));
    }

    lineGrapher.render();

    TimeUnit.SECONDS.sleep(45);
  }

  private static Namespace parseArgs(String[] args) throws ArgumentParserException {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("Portfolio Comparer", true)
        .description("Compare performance of portfolio wrt any ticker(s)");

    parser.addArgument("--csv").required(true)
        .help("path of Yahoo Finance csv file with portfolio data");

    String[] defaultTickers = new String[0];
    parser.addArgument("--compareTo").nargs("+").setDefault(defaultTickers)
        .help("Tickers of stocks to compare with.");

    return parser.parseArgs(args);
  }
}
