package NTM2.Memory.Addressing;

import NTM2.Controller.Sigmoid;
import NTM2.Controller.Unit;
import NTM2.Controller.UnitFactory;
import NTM2.Memory.Addressing.Content.ContentAddressing;
import NTM2.Memory.HeadSetting;

public class GatedAddressing   
{
    public final Unit _gate;
    public final HeadSetting _oldHeadSettings;
    public final ContentAddressing ContentVector;
    public final Unit[] GatedVector;
    public final int _memoryCellCount;
    //Interpolation gate
    public final double _gt;
    public final double _oneminusgt;

    public GatedAddressing(Unit gate, ContentAddressing contentAddressing, HeadSetting oldHeadSettings) {
        _gate = gate;
        ContentVector = contentAddressing;
        _oldHeadSettings = oldHeadSettings;
        Unit[] contentVector = ContentVector.ContentVector;
        _memoryCellCount = contentVector.length;
        GatedVector = UnitFactory.getVector(_memoryCellCount);
        //Implementation of focusing by location - page 8 part 3.3.2. Focusing by Location
        _gt = Sigmoid.getValue(_gate.Value);
        _oneminusgt = (1 - _gt);
        for (int i = 0;i < _memoryCellCount;i++)
        {
            GatedVector[i].Value = (_gt * contentVector[i].Value) + (_oneminusgt * _oldHeadSettings.addressingVector[i].Value);
        }
    }

    public void backwardErrorPropagation() {
        Unit[] contentVector = ContentVector.ContentVector;
        double gradient = 0;
        for (int i = 0;i < _memoryCellCount;i++)
        {
            Unit oldHeadSetting = _oldHeadSettings.addressingVector[i];
            Unit contentVectorItem = contentVector[i];
            Unit gatedVectorItem = GatedVector[i];
            gradient += (contentVectorItem.Value - oldHeadSetting.Value) * gatedVectorItem.gradient;
            contentVectorItem.gradient += _gt * gatedVectorItem.gradient;
            oldHeadSetting.gradient += _oneminusgt * gatedVectorItem.gradient;
        }
        _gate.gradient += gradient * _gt * _oneminusgt;
    }

}


