package com.example.launchexample;



public class Launcher {

    private double springConstant;
    private double braceAngle;
    private double beamLength;


    public Launcher(double springConstant, double braceAngle, double beamLength)
    {
        this.springConstant = Math.abs(springConstant);
        this.braceAngle = Math.abs(braceAngle);
        this.braceAngle = Math.abs(Math.PI * this.braceAngle * (1.0/180));
        this.beamLength = Math.abs(beamLength);
    }

    private void UpdateConditions(double sC, double bA, double bL)
    {
        this.springConstant = Math.abs(sC);
        this.braceAngle = Math.abs(bA);
        this.beamLength = Math.abs(bL);
    }

    public void UpdateSpringConstant(double springConstant)
    {
        UpdateConditions(springConstant,this.braceAngle, this.beamLength);
    }

    public double UpdateBraceAngle(double braceAngle)
    {
        braceAngle = Math.max(braceAngle,1);
        double radianBraceAngle = braceAngle * Math.PI * (1.0/180);
        UpdateConditions(this.springConstant, radianBraceAngle, this.beamLength);

        return braceAngle;
    }

    public void UpdateBeamLength(double beamLength)
    {
        UpdateConditions(this.springConstant, this.braceAngle, beamLength);
    }

    public double SpringConstant()
    {
        return this.springConstant;
    }

    public double BraceAngle()
    {
        return this.braceAngle;
    }

    public double BeamLength()
    {
        return this.beamLength;
    }


}
