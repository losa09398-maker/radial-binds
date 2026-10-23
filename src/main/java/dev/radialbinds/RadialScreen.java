package dev.radialbinds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.*;

public class RadialScreen extends Screen {
    static final List<String> PLAYER_BINDS = new ArrayList<>();
    static final List<String> SELF_BINDS = new ArrayList<>();
    private static boolean playerMode = true;
    private final Entity target;
    private String selected = null;
    private boolean previousDown = true;
    private int cx, cy, radius = 105;
    protected RadialScreen(Entity target) { super(Text.literal("Radial Binds")); this.target = target; }
    private List<String> binds() { return playerMode ? PLAYER_BINDS : SELF_BINDS; }
    @Override protected void init() {
        cx = width / 2; cy = height / 2;
        addDrawableChild(ButtonWidget.builder(Text.literal(playerMode ? "Режим: Игроки" : "Режим: Для себя"), b -> { playerMode = !playerMode; selected = null; clearAndInit(); }).dimensions(cx-58, cy-15, 116, 30).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> { if (client != null) client.setScreen(new AddBindScreen(this, playerMode)); }).dimensions(cx-14, cy+radius+12, 28, 22).build());
    }
    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        cx = width/2; cy=height/2;
        ctx.fill(cx-radius-8, cy-radius-8, cx+radius+8, cy+radius+8, 0xAA10131B);
        ctx.fill(cx-44,cy-14,cx+44,cy+14,0xEE303846);
        ctx.drawCenteredTextWithShadow(textRenderer, playerMode ? "Игроки" : "Для себя", cx, cy-5, 0xFFFFFFFF);
        List<String> list=binds(); int n=list.size();
        for (int i=0;i<n;i++) {
            double a=-Math.PI/2 + 2*Math.PI*i/n;
            int x=cx+(int)(Math.cos(a)*radius), y=cy+(int)(Math.sin(a)*radius);
            boolean hover=Math.hypot(mouseX-x,mouseY-y)<30;
            if(hover) selected=list.get(i);
            ctx.fill(x-38,y-13,x+38,y+13, hover?0xFF4D8CFF:0xDD252B36);
            String s=list.get(i); if(s.length()>12)s=s.substring(0,12)+"…";
            ctx.drawCenteredTextWithShadow(textRenderer,s,x,y-4,0xFFFFFFFF);
        }
        if (n==0) ctx.drawCenteredTextWithShadow(textRenderer,"Нажми + чтобы добавить",cx,cy+radius+43,0xFFCCCCCC);
        super.render(ctx,mouseX,mouseY,delta);
    }
    @Override public boolean mouseClicked(double mouseX,double mouseY,int button) {
        if(button==1) { String hit=hit(mouseX,mouseY); if(hit!=null) binds().remove(hit); return true; }
        return super.mouseClicked(mouseX,mouseY,button);
    }
    private String hit(double mx,double my) {
        List<String> list=binds(); int n=list.size(); if(n==0)return null;
        for(int i=0;i<n;i++){double a=-Math.PI/2+2*Math.PI*i/n; double x=cx+Math.cos(a)*radius,y=cy+Math.sin(a)*radius;if(Math.hypot(mx-x,my-y)<38)return list.get(i);} return null;
    }
    @Override public void tick() {
        if(client==null)return;
        long w=client.getWindow().getHandle();
        boolean down=GLFW.glfwGetMouseButton(w,GLFW.GLFW_MOUSE_BUTTON_MIDDLE)==GLFW.GLFW_PRESS;
        if(previousDown && !down) {
            String cmd=hit(client.mouse.getX()*width/client.getWindow().getWidth(),client.mouse.getY()*height/client.getWindow().getHeight());
            RadialBindsClient.runCommand(cmd,playerMode,target);
            close();
        }
        previousDown=down;
    }
    @Override public boolean shouldPause(){return false;}
    @Override public boolean shouldCloseOnEsc(){return true;}
}
