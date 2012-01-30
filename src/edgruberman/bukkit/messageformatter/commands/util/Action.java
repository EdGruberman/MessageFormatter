package edgruberman.bukkit.messageformatter.commands.util;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Single action of a command.
 */
public abstract class Action {

    protected Handler handler;
    protected String name;
    protected String permission;

    /**
     * Single action for an handler which results in a simple command that
     * uses only plugin.command for permission.
     * 
     * @param plugin command owner
     * @param label command label
     */
    protected Action(final JavaPlugin plugin, final String label) {
        this(new Handler(plugin, label), label, (String) null);
    }

    /**
     * Multiple sub-actions for an handler which results in a complex command
     * that uses plugin.command.action permission for each action.
     * 
     * @param handler action parent
     * @param name action name (first parameter after command label)
     */
    protected Action(final Handler handler, final String name) {
        this(handler, name, handler.permission + "." + name.toLowerCase());
    }

    /**
     * Custom permission assignment.
     * 
     * @param handler action parent
     * @param name action name
     * @param permission custom permission required to use action; null if relying on handler permission only
     */
    protected Action(final Handler handler, final String name, final String permission) {
        this.handler = handler;
        this.name = name.toLowerCase();
        this.permission = permission;
        this.handler.actions.add(this);
    }

    /**
     * Determines if this action is applicable to be called for the given
     * arguments based on the first argument matching the action name.
     * Override this method for more complex action assignment.
     * 
     * @param context execution context
     * @return true if this action should be performed; false otherwise
     */
    public boolean matches(final Context context) {
        if (this.handler.actions.size() == 1) return true;

        if (context.arguments.size() == 0) return false;

        if (context.arguments.get(0).equalsIgnoreCase(this.name)) return true;

        return false;
    }

    /**
     * Performs this action.
     * 
     * @param context execution context
     * @return true if the action was performed as expected
     */
    public abstract boolean perform(final Context context);
}