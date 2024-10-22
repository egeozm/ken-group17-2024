package src.main.dataHandle;

public class Student {
    private final int studentID;
    private final String neuroSynapticInterfaceLevel;
    private final double plasmaConductivityQuotient;
    private final int chronoAdaptationRate;
    private final char telepathicSynchronisationIndex;
    private final double aethericResonanceCapacity;

    public Student (int studentID, String neuroSynapticInterfaceLevel,double plasmaConductivityQuotient, int chronoAdaptationRate, char telepathicSynchronisationIndex, double aethericResonanceCapacity){
        this.studentID = studentID;
        this.neuroSynapticInterfaceLevel = neuroSynapticInterfaceLevel;
        this.plasmaConductivityQuotient = plasmaConductivityQuotient;
        this.chronoAdaptationRate = chronoAdaptationRate;
        this.telepathicSynchronisationIndex = telepathicSynchronisationIndex;
        this.aethericResonanceCapacity = aethericResonanceCapacity;
    }

    public int getStudentID(){
        return studentID;
    }

    public String getNeuroSynapticInterfaceLevel(){
        return neuroSynapticInterfaceLevel;
    }

    public double getPlasmaConductivityQuotient(){
        return plasmaConductivityQuotient;
    }

    public int getChronoAdaptationRate(){
        return chronoAdaptationRate;
    }

    public char getTelepathicSynchronisationIndex(){
        return telepathicSynchronisationIndex;
    }

    public double getAethericResonanceCapacity(){
        return aethericResonanceCapacity;
    }

}
