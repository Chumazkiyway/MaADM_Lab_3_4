package lab;

import java.util.Arrays;
import java.util.Comparator;

public class Solver {

    public static final int COUNT = 10; // Default count
    private static final int N = 3; // My variant, count of passengers
    private static final int ProbabilityBetasColumnsCount = 3; // Count of probability betas columns

    // Source data
    private double mCostOfOperation;
    private double mFare;
    private double mFine;
    private int mEmptyBuses;
    private int mLackOfBuses;
    private double mAlpha;

    private double[][] mMatrixOfProfits; // Matrix of profits

    private double[] mSubjectiveProbabilities;
    private double[] mProbabilitiesOfSolution;

    Solver(double costOfOperation, double fare, double fine, int emptyBuses, int lackOfBuses, double alpha) {
        // Initialize source data
        mCostOfOperation = costOfOperation;
        mFare = fare;
        mFine = fine;
        mEmptyBuses = emptyBuses;
        mLackOfBuses = lackOfBuses;
        mAlpha = alpha;

        // Calculate subjective probabilities and probabilities of solution
        mSubjectiveProbabilities = new double[COUNT];
        mProbabilitiesOfSolution = new double[COUNT];

        for (var i = 0; i < mSubjectiveProbabilities.length; i++) {
            mSubjectiveProbabilities[i] = getSubjectiveProbability(i + 1);
            mProbabilitiesOfSolution[i] = getProbabilityOfSolution(i + 1);
        }
    }

    private double getSubjectiveProbability(int index) {
        return (12.00 - index) * index / 286.00;
    }

    private double getProbabilityOfSolution(int index) {
        return 1.00 - 0.01 * (N + index);
    }


    public int[] getCountOfBusses() {
        // Top header
        int[] mBusses = new int[COUNT];
        for (var i = 0; i < COUNT; i++) {
            mBusses[i] = N + i;
        }
        return mBusses;
    }

    public int[] getCountOfPassengers() {
        // Left header
        int[] mPassengers = new int[COUNT];
        for (var i = 0; i < COUNT; i++) {
            mPassengers[i] = N + i;
        }
        return mPassengers;
    }


    public double[][] getMatrixOfProfits() {
        mMatrixOfProfits = new double[COUNT][];

        for (var i = 0; i < COUNT; i++) {
            mMatrixOfProfits[i] = new double[COUNT];

            for (var j = 0; j < COUNT; j++) {

                // Calcucate count of passengers and buses
                var countOfPassengers = j + N;
                var countOfBuses = i + N;

                if (countOfPassengers == countOfBuses) {
                    mMatrixOfProfits[i][j] = (mFare - mCostOfOperation) * countOfBuses;

                } else if (countOfPassengers > countOfBuses) {

                    var extraPassengers = countOfPassengers - countOfBuses;
                    // We don't need to pay a fare for extra passengers (> n2 => fine for each bus)
                    mMatrixOfProfits[i][j] = extraPassengers > mLackOfBuses
                            ? (mFare - mCostOfOperation) * countOfBuses - (extraPassengers - mLackOfBuses) * mFine
                            : (mFare - mCostOfOperation) * countOfBuses;

                } else {

                    var extraBusesCount = countOfBuses - countOfPassengers;
                    // We don't need to pay for extra buses (n1 => fare for each bus)
                    if (extraBusesCount > mEmptyBuses) {
                        // We don't pay for transferred buses and they bring us profit
                        var transferredBuses = extraBusesCount - mEmptyBuses;
                        mMatrixOfProfits[i][j] = mFare * countOfPassengers -
                                mCostOfOperation * (countOfBuses - transferredBuses) +
                                transferredBuses * (mFare - mCostOfOperation);
                    } else {
                        mMatrixOfProfits[i][j] = mFare * countOfPassengers - mCostOfOperation * countOfBuses;
                    }
                }
            }
        }

        return mMatrixOfProfits;
    }

    public double getMiniMaxCriterion() {
        var fMiniMax = new double[COUNT];
        for (var i = 0; i < COUNT; i++) {
            fMiniMax[i] = Arrays.stream(mMatrixOfProfits[i]).min().getAsDouble();
        }
        return Arrays.stream(fMiniMax).max().getAsDouble();
    }

    public double getLaplaceCriterion() {
        var fLaplas = new double[COUNT];

        for (var i = 0; i < COUNT; i++) {
            fLaplas[i] = Arrays.stream(mMatrixOfProfits[i]).average().getAsDouble();
        }
        return Arrays.stream(fLaplas).max().getAsDouble();
    }

    public double getSavageCriterion() {
        // Find max alternative for each column
        var maxAlternatives = new double[COUNT];

        for (var i = 0; i < COUNT; i++) {
            var max = Double.MIN_VALUE;

            for (var j = 0; j < COUNT; j++) {
                if (mMatrixOfProfits[j][i] > max)
                    max = mMatrixOfProfits[j][i];
            }

            maxAlternatives[i] = max;
        }

        // Calculate regret matrix
        var regretMatrix = new double[COUNT][];
        for (var i = 0; i < COUNT; i++) {
            regretMatrix[i] = new double[COUNT];
            for (var j = 0; j < COUNT; j++)
                regretMatrix[i][j] = maxAlternatives[j] - mMatrixOfProfits[i][j];
        }

        // Find max values in rows of the regret matrix
        var fMax = new double[COUNT];
        for (var i = 0; i < COUNT; i++)
            fMax[i] = Arrays.stream(regretMatrix[i]).max().getAsDouble();

        return Arrays.stream(fMax).min().getAsDouble();
    }

    public double getHurwitzCriterion() {
        var maxsProbability = new double[COUNT];
        var minsProbability = new double[COUNT];
        var resultProbability = new double[COUNT];

        for (var i = 0; i < COUNT; i++) {
            maxsProbability[i] = Arrays.stream(mMatrixOfProfits[i]).max().getAsDouble() * mAlpha;
            minsProbability[i] = Arrays.stream(mMatrixOfProfits[i]).min().getAsDouble() * (1.00 - mAlpha);
            resultProbability[i] = maxsProbability[i] + minsProbability[i];
        }

        return Arrays.stream(resultProbability).max().getAsDouble();
    }

    boolean containsNegative() {
        return Arrays.stream(mMatrixOfProfits).anyMatch(row -> Arrays.stream(row).anyMatch(cell -> cell <0));
    }

    public double[][] getChangedWithNegativeMatrixOfProfits() {
        var maxNegative = Arrays.stream(mMatrixOfProfits)
                .map(x -> Arrays.stream(x).min().getAsDouble())
                .min(Comparator.comparingDouble(x -> x)).get();// Find the lowest negative element

        var maxNegativeAbsolute = Math.abs(maxNegative); // Calculate ablsolute value

        var changedMatrixOfProfits = new double[COUNT][];
        for (var i = 0; i < COUNT; i++) {
            changedMatrixOfProfits[i] = new double[COUNT];

            for (var j = 0; j < COUNT; j++) {

                changedMatrixOfProfits[i][j] = mMatrixOfProfits[i][j] + maxNegativeAbsolute + 1;
            }
        }

        return changedMatrixOfProfits;
    }

    public double getMultiplicationCriterion(double[][] matrixOfProfits) {
        var multiplications = new double[COUNT];
        for (var i = 0; i < COUNT; i++) {
            multiplications[i] = Arrays.stream(matrixOfProfits[i]).reduce(1, (a, b) -> a * b);
        }

        return Arrays.stream(multiplications).max().getAsDouble();
    }

    public double getBayesLaplaceCriterion() {
        var resultProbability = new double[COUNT];
        for (var i = 0; i < COUNT; i++) {
            var multiplication = 0.00;

            for (var j = 0; j < COUNT; j++) {

                multiplication += mMatrixOfProfits[i][j] * mSubjectiveProbabilities[j] *
                        mProbabilitiesOfSolution[i];
            }

            resultProbability[i] = multiplication;
        }

        return Arrays.stream(resultProbability).max().getAsDouble();
    }

    public double getHodgeLehmannCriterion() {
        var firstProbabilities = new double[COUNT];
        var secondProbabilities = new double[COUNT];
        var resultProbabilities = new double[COUNT];

        for (var i = 0; i < COUNT; i++) {
            var multiplication = 0.00;

            for (var j = 0; j < COUNT; j++) {

                multiplication += mMatrixOfProfits[i][j] * mSubjectiveProbabilities[j] *
                        mProbabilitiesOfSolution[i];
            }

            firstProbabilities[i] = multiplication * mAlpha;
            secondProbabilities[i] = (1.00 - mAlpha) * Arrays.stream(mMatrixOfProfits[i]).min().getAsDouble();
            resultProbabilities[i] = firstProbabilities[i] + secondProbabilities[i];
        }

        return Arrays.stream(resultProbabilities).max().getAsDouble();
    }

    public double getHermeierCriterion() {
        var maxOrMinValues = new double[COUNT];
        var result = 0.00;
        if (containsNegative()) {
            // Max firstly
            for (var i = 0; i < COUNT; i++) {
                // Find max value for each row
                var max = Double.MIN_VALUE;

                for (var j = 0; j < COUNT; j++) {

                    var value = mMatrixOfProfits[i][j] * mSubjectiveProbabilities[j] *
                            mProbabilitiesOfSolution[i];
                    if (value > max)
                        max = value;
                }
                maxOrMinValues[i] = max;
            }
            // Result is min of max values
            result = Arrays.stream(maxOrMinValues).min().getAsDouble();
        } else {
            // Min firstly
            for (var i = 0; i < COUNT; i++) {
                // Find min value for each row
                var min = Double.MAX_VALUE;

                for (var j = 0; j < COUNT; j++) {

                    var value = mMatrixOfProfits[i][j] * mSubjectiveProbabilities[j] *
                            mProbabilitiesOfSolution[i];

                    if (value < min)
                        min = value;
                }

                maxOrMinValues[i] = min;
            }
            // Result is max of min values
            result = Arrays.stream(maxOrMinValues).max().getAsDouble();
        }

        return result;
    }

    public double getMostProbableResultCriterion() {
        var resultProbabilities = new double[COUNT];

        for (var i = 0; i < COUNT; i++) {
            var multiplication = 0.00;

            for (var j = 0; j < COUNT; j++) {
                multiplication += mSubjectiveProbabilities[j] * mProbabilitiesOfSolution[i];
            }
            resultProbabilities[i] = multiplication;
        }

        return Arrays.stream(resultProbabilities).max().getAsDouble();
    }

}
