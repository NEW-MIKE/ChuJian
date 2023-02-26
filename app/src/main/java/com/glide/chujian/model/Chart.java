package com.glide.chujian.model;

public class Chart {
    public Double mDx;
    public Double mDy;
    public Double mRADistanceRaw;
    public Double mDEDDistanceRaw;
    public Double mRADistanceGuide;
    public Double mDECDistanceGuide;
    public Integer mRADuration;
    public Integer mDECDuration;
    public String mRADirection;
    public String mDECDirection;
    public Double mStarMass;
    public Double mSNR;
    public Double mHFD;
    public Double mAvgDist;

    public Chart(Double dx, Double dy, Double RADistanceRaw, Double decDistanceRaw,
                 Double RADistanceGuide, Double decDistanceGuide, Integer RADuration,
                 Integer DECDuration, String RADirection, String DECDirection,
                 Double starMass, Double SNR, Double HFD, Double avgDist) {
        this.mDx = dx;
        this.mDy = dy;
        this.mRADistanceRaw = RADistanceRaw;
        mDEDDistanceRaw = decDistanceRaw;
        this.mRADistanceGuide = RADistanceGuide;
        mDECDistanceGuide = decDistanceGuide;
        this.mRADuration = RADuration;
        this.mDECDuration = DECDuration;
        this.mRADirection = RADirection;
        this.mDECDirection = DECDirection;
        mStarMass = starMass;
        this.mSNR = SNR;
        this.mHFD = HFD;
        mAvgDist = avgDist;
    }

    public Chart() {
        this(null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }
}
