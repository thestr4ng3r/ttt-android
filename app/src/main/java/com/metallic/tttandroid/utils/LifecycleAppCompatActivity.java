package com.metallic.tttandroid.utils;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.support.v7.app.AppCompatActivity;

public abstract class LifecycleAppCompatActivity extends AppCompatActivity implements LifecycleRegistryOwner
{
    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    public LifecycleAppCompatActivity()
    {
    }

    public LifecycleRegistry getLifecycle()
    {
        return this.mRegistry;
    }
}
