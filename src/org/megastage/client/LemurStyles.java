package org.megastage.client;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.Styles;

public class LemurStyles {

    public static final String TITLE_ID = "title";
    public static final String MESSAGE_ID = "message";
    public static final String MENU_ID = "menu";
    public static final String SUBMENU_ID = "submenu";
    public static final String MENU_TITLE_ID = "menu.title";
    public static final String EDIT_LABEL_ID = "edit.label";
    public static final String EDIT_FIELD_ID = "edit.field";

    public static void initializeStyles( Styles styles ) {

        // Set the message font size to be the same (by default) for any
        // any style.
        Attributes title = styles.getSelector(TITLE_ID, null);
        title.set("fontSize", 48f);
        title.set("textHAlignment", HAlignment.Center);

        Attributes message = styles.getSelector(MESSAGE_ID, null);
        message.set("fontSize", 32f);

        // Then the "retro"-specific styles

        // Common background for some panels and buttons
        TbtQuadBackgroundComponent border
            = TbtQuadBackgroundComponent.create("/com/simsilica/lemur/icons/border.png",
                                                1, 2, 2, 3, 3, 0, false);

        Attributes menu = styles.getSelector(MENU_ID, "retro");
        border.setColor(ColorRGBA.Blue);
        menu.set("background", border.clone());

        Attributes menuTitle = styles.getSelector(MENU_TITLE_ID, "retro");
        menuTitle.set("background", border.clone());
        menuTitle.set("insets", new Insets3f(1, 1, 10, 1));
        menuTitle.set("fontSize", 48f); // would inherit from regular title but I feel
                                        // like being specific

        Attributes button = styles.getSelector(Button.ELEMENT_ID, "retro");
        border.setColor(ColorRGBA.Cyan);
        button.set("background", border.clone());
        button.set("insets", new Insets3f(1, 5, 10, 5));
        button.set("textHAlignment", HAlignment.Center);
        button.set("fontSize", 32f);

        Attributes subpanel = styles.getSelector(SUBMENU_ID, "retro");
        subpanel.set("insets", new Insets3f(1, 5, 10, 5));
        //subpanel.set("insets", new Insets3f(1, 135, 10, 35)); 
        subpanel.set("background", border.clone());
        
        Attributes editLabel = styles.getSelector(EDIT_LABEL_ID, "retro");
        editLabel.set("fontSize", 24f);
        editLabel.set("insets", new Insets3f(5, 5, 5, 5)); 
        editLabel.set("textHAlignment", HAlignment.Right);
        //editLabel.set("color", new ColorRGBA(0, 0, 0.2f, 1)); 
        editLabel.set("color", ColorRGBA.White); 
        editLabel.set("shadowOffset", new Vector3f(1, -1, 0.1f)); 
        //editLabel.set("shadowColor", new ColorRGBA(0.8f, 0.9f, 0.9f, 1));
        editLabel.set("shadowColor", ColorRGBA.Black);  
     
        Attributes editField = styles.getSelector(TextField.ELEMENT_ID, "retro");
        editField.set("fontSize", 24f);
        editField.set("background", border.clone());     
    
    }
}    
