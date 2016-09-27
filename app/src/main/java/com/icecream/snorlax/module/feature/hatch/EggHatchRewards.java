package com.icecream.snorlax.module.feature.hatch;

class EggHatchRewards {
    private int experienceAwarded;
    private int candyAwarded;
    private int stardustAwarded;

    public EggHatchRewards(int experienceAwarded, int candyAwarded, int stardustAwarded) {
        this.experienceAwarded = experienceAwarded;
        this.candyAwarded = candyAwarded;
        this.stardustAwarded = stardustAwarded;
    }

    public int getExperienceAwarded() {
        return experienceAwarded;
    }

    public int getCandyAwarded() {
        return candyAwarded;
    }

    public int getStardustAwarded() {
        return stardustAwarded;
    }
}