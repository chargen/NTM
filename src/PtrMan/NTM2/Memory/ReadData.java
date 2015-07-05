package NTM2.Memory;

import NTM2.Controller.Unit;
import org.javatuples.Pair;

import java.util.function.Function;

public class ReadData   
{
    public final HeadSetting head;
    public final Unit[] read;
    private final NTMMemory memory;
    private final int cellWidth;
    private final int cellHeight;

    public ReadData(HeadSetting head, NTMMemory mem) {
        this.head = head;
        memory = mem;
        cellWidth = memory.memoryWidth;
        cellHeight = memory.memoryHeight;

        read = new Unit[cellWidth];
        for (int i = 0;i < cellWidth;i++) {
            double temp = 0.0;
            for (int j = 0;j < cellHeight;j++) {
                temp += head.addressingVector[j].value * mem.data[j][i].value;
            }
            //if (double.IsNaN(temp))
            //{
            //    throw new Exception("Memory error");
            //}
            read[i] = new Unit(temp);
        }
    }

    public void backwardErrorPropagation() {
        for (int i = 0;i < cellHeight;i++) {
            double gradient = 0.0;
            Unit[] dataVector = memory.data[i];
            Unit addressingVectorUnit = head.addressingVector[i];
            for (int j = 0;j < cellWidth;j++) {
                double readUnitGradient = read[j].grad;
                Unit dataUnit = dataVector[j];
                gradient += readUnitGradient * dataUnit.value;
                dataUnit.grad += readUnitGradient * addressingVectorUnit.value;
            }
            addressingVectorUnit.grad += gradient;
        }
    }

    public static ReadData[] getVector(int x, Function<Integer, Pair<HeadSetting, NTMMemory>> paramGetters) {
        ReadData[] vector = new ReadData[x];
        for (int i = 0;i < x;i++) {
            Pair<HeadSetting, NTMMemory> parameters = paramGetters.apply(i);
            vector[i] = new ReadData(parameters.getValue0(), parameters.getValue1());
        }
        return vector;
    }

}


