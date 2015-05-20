package com.tutors.varsity.util.otto;

import com.squareup.otto.Bus;

public final class ApplicationBus {

    private static final Bus sBus = new Bus();

    public static Bus getInstance() {
        return sBus;
    }

    private ApplicationBus() {
        // No instances.
    }
}
