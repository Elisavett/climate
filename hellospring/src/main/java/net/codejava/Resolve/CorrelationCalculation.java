package net.codejava.Resolve;

import net.codejava.Resolve.Model.Corr;
import net.codejava.Resolve.Model.Phase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Класс служит для расчета корреляции
 *
 * @version 1.0
 */
public class CorrelationCalculation implements Callable<Corr> {
    private final int firstIndex;
    private final int totalNumber;
    private final double[] correlation;
    private double[] firstStation;
    private double[] secondStation;
    List<Future<Phase>> arrayPhase;

    public CorrelationCalculation(int firstIndex, int totalNumber, List<Future<Phase>> arrayPhase) {
        this.firstIndex = firstIndex;
        this.totalNumber = totalNumber;
        this.correlation = new double[totalNumber - firstIndex - 1];
        this.arrayPhase = arrayPhase;
    }


    private double[] loadStantion(int index) throws ExecutionException, InterruptedException {
        double[] array;
        Phase phase = (Phase) arrayPhase.get(index).get();
        array = phase.getArray();
        return array;
    }

    private double correlationCalc() {
        double average1 = 0, average2 = 0;
        int num = firstStation.length;
        for (int i = 0; i < num; i++) {
            average1 += firstStation[i];
            average2 += secondStation[i];
        }
        average1 = average1 / num;
        average2 = average2 / num;

        double part1 = 0;
        double part2 = 0;
        double part3 = 0;
        for (int i = 0; i < num; i++) {
            double dif1 = firstStation[i] - average1;
            double dif2 = secondStation[i] - average2;
            part1 += dif1 * dif2;
            part2 += dif1 * dif1;
            part3 += dif2 * dif2;
        }
        double part4 = Math.sqrt(part2 * part3);
        return part1 / part4;
    }


    @Override
    public Corr call() throws Exception {
        firstStation = loadStantion(firstIndex);
        int counter = 0;
        for (int i = firstIndex + 1; i < totalNumber; i++) {
            secondStation = loadStantion(i);
            correlation[counter] = correlationCalc();
            counter++;
        }
        Corr corr = new Corr(correlation);
        return corr;
    }
}
