package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.BulkChange;
import hudson.model.Run;
import jenkins.model.RunAction2;

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.logging.Logger;

public class CredentialsIdAction extends AbstractInvisibleRunAction2 implements RunAction2 {

    private static final Logger LOGGER = Logger.getLogger(CredentialsIdAction.class.getName());

    CredentialsIdAction() {
        super();
    }

    @Deprecated
    public CredentialsIdAction(Run run) {
        setRun(run);
    }

    protected static void push(@NonNull Run<?, ?> run, @NonNull String item) throws IOException {
        AbstractInvisibleRunAction2.push(run, CredentialsIdAction.class, item);
    }

    @Deprecated
    public void push(String credentialsId) throws IOException {
        if (run == null) {
            LOGGER.warning("run is null, cannot push");
            return;
        }
        synchronized (run) {
            BulkChange bc = new BulkChange(run);
            try {
                CredentialsIdAction action = run.getAction(CredentialsIdAction.class);
                if (action == null) {
                    action = new CredentialsIdAction(run);
                    run.addAction(action);
                }
                action.stack.push(credentialsId);
                bc.commit();
            } finally {
                bc.abort();
            }
        }
    }

    @Deprecated
    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    public String pop() throws IOException {
        if (run == null) {
            LOGGER.warning("run is null, cannot pop");
            return null;
        }
        synchronized (run) {
            BulkChange bc = new BulkChange(run);
            try {
                CredentialsIdAction action = run.getAction(CredentialsIdAction.class);
                if (action == null) {
                    action = new CredentialsIdAction(run);
                    run.addAction(action);
                }
                String credentialsId = action.stack.pop();
                bc.commit();
                return credentialsId;
            } finally {
                bc.abort();
                return null;
            }
        }
    }

    public String getCredentialsId() {
        try {
            return stack.peek();
        } catch (EmptyStackException e) {
            return null;
        }
    }
}
