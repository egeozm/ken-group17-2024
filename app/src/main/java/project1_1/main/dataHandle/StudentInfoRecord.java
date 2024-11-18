package project1_1.main.dataHandle;

public class StudentInfoRecord {
    private final int studentID;
    private final String neuroSynapticInterfaceLevel;
    private final double plasmaConductivityQuotient;
    private final int chronoAdaptationRate;
    private final char telepathicSynchronisationIndex;
    private final Double aethericResonanceCapacity;

    public StudentInfoRecord(int studentID, String neuroSynapticInterfaceLevel, double plasmaConductivityQuotient, int chronoAdaptationRate, char telepathicSynchronisationIndex, Double aethericResonanceCapacity) {
        this.studentID = studentID;
        this.neuroSynapticInterfaceLevel = neuroSynapticInterfaceLevel;
        this.plasmaConductivityQuotient = plasmaConductivityQuotient;
        this.chronoAdaptationRate = chronoAdaptationRate;
        this.telepathicSynchronisationIndex = telepathicSynchronisationIndex;
        this.aethericResonanceCapacity = aethericResonanceCapacity;
    }

    public int getStudentID() {
        return studentID;
    }

    public String getNeuroSynapticInterfaceLevel() {
        return neuroSynapticInterfaceLevel;
    }

    public double getPlasmaConductivityQuotient() {
        return plasmaConductivityQuotient;
    }

    public int getChronoAdaptationRate() {
        return chronoAdaptationRate;
    }

    public char getTelepathicSynchronisationIndex() {
        return telepathicSynchronisationIndex;
    }

    public double getAethericResonanceCapacity() {
        return aethericResonanceCapacity;
    }

    @Override
    public String toString() {
        return "StudentID: " + studentID +
                ", NeuroSynapticInterfaceLevel: " + neuroSynapticInterfaceLevel +
                ", PlasmaConductivityQuotient: " + plasmaConductivityQuotient +
                ", ChronoAdaptationRate: " + chronoAdaptationRate +
                ", TelepathicSynchronisationIndex: " + telepathicSynchronisationIndex +
                ", AethericResonanceCapacity: " + aethericResonanceCapacity;
    }

}
