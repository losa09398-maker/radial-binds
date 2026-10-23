package dev.radialbinds;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AddBindScreen extends Screen {
    private final RadialScreen parent; private final boolean playerMode; private TextFieldWidget field;
    protected AddBindScreen(RadialScreen parent, boolean playerMode){super(Text.literal("Добавить бинд"));this.parent=parent;this.playerMode=playerMode;}
    @Override protected void init(){field=new TextFieldWidget(textRenderer,width/2-110,height/2-12,220,24,Text.literal("Команда"));field.setMaxLength(100);field.setPlaceholder(Text.literal("Например: /report "));addDrawableChild(field);
        addDrawableChild(ButtonWidget.builder(Text.literal("Добавить"),b->{String s=field.getText().trim();if(!s.isEmpty()){if(playerMode)RadialScreen.PLACEHOLDER();} }).dimensions(width/2-50,height/2+25,100,20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Назад"),b->client.setScreen(parent)).dimensions(width/2-50,height/2+52,100,20).build());}
    @Override public void render(DrawContext ctx,int x,int y,float d){renderBackground(ctx,x,y,d);ctx.drawCenteredTextWithShadow(textRenderer,"Введи команду (без ника игрока)",width/2,height/2-42,0xFFFFFFFF);super.render(ctx,x,y,d);}
}
