package asatsuki256.germplasm.core.machine;

import static asatsuki256.germplasm.core.GermplasmCore.UNLOC_PREFIX;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import asatsuki256.germplasm.core.GermplasmCore;
import asatsuki256.germplasm.core.tileentity.TileReactor;
import asatsuki256.germplasm.core.tileentity.TileReactor.FluidHandlerReactor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class GuiReactor extends GuiContainer {
	
	private final IItemHandler inventory;
	private final FluidHandlerReactor fluidHandler;
	TileReactor tile;
	
	private static final ResourceLocation TEXTURE0 = new ResourceLocation(GermplasmCore.MODID, "textures/gui/container/reactor.png");
	
	public GuiReactor(int x, int y, int z, InventoryPlayer inventoryPlayer, TileReactor tile) {
        super(new ContainerReactor(x, y, z, inventoryPlayer, tile));
        xSize = 178;
        ySize = 220;
        inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        fluidHandler = tile.getFluidHandlerReactor();
        this.tile = tile;
    }
	
	@Override
    public void initGui() {
    	super.initGui();
    }
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        FluidTank reagentTank = fluidHandler.getReagentTank();
        FluidTank resultTank = fluidHandler.getResultTank();
        if(mouseX >= guiLeft+27-1 && mouseX < guiLeft+27+16+1 && mouseY >= guiTop+34-1 && mouseY < guiTop+34+64+1 && reagentTank.getFluid() != null) {
        	List<String> desc = new ArrayList<String>();
        	desc.add(reagentTank.getFluid().getLocalizedName());
        	desc.add(reagentTank.getFluid().amount + " / " + reagentTank.getCapacity() + " mB");
        	drawHoveringText(desc, mouseX, mouseY);
        } else if(mouseX >= guiLeft+135-1 && mouseX < guiLeft+135+16+1 && mouseY >= guiTop+34-1 && mouseY < guiTop+34+64+1 && resultTank.getFluid() != null) {
        	List<String> desc = new ArrayList<String>();
        	desc.add(resultTank.getFluid().getLocalizedName());
        	desc.add(resultTank.getFluid().amount + " / " + reagentTank.getCapacity() + " mB");
        	drawHoveringText(desc, mouseX, mouseY);
        } else if (mouseX >= guiLeft+7-1 && mouseX < guiLeft+7+32+1 && mouseY >= guiTop+25-1 && mouseY < guiTop+25+4+1) {
        	List<String> desc = new ArrayList<String>();
        	desc.add("Forge Energy");
        	desc.add(String.format("%,3d", tile.getEnergy().getEnergyStored()) + " / " + String.format("%,3d", tile.getEnergy().getMaxEnergyStored()) + " FE");
        	drawHoveringText(desc, mouseX, mouseY);
        } else {
        	this.renderHoveredToolTip(mouseX, mouseY);
        }
    }
	
	@Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);
        this.fontRenderer.drawString(I18n.format(UNLOC_PREFIX + "gui.bioreactor.name"), 8, 8, 0x404040);
    }
	
	@Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseZ) {
        this.mc.renderEngine.bindTexture(TEXTURE0);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 177, 219);
        //progress
        this.drawTexturedModalRect(guiLeft + 80, guiTop + 58, 192, 0, MathHelper.ceil(tile.getProgress() * 18), 15);
        //energy
        float energy = (float)tile.getEnergy().getEnergyStored() / (float)tile.getEnergy().getMaxEnergyStored();
        this.drawTexturedModalRect(guiLeft + 7, guiTop + 25, 192, 32, MathHelper.ceil(energy * 32), 4);
        //fluid
        FluidTank reagentTank = fluidHandler.getReagentTank();
        FluidTank resultTank = fluidHandler.getResultTank();
        if(reagentTank.getFluid() != null) {
        	drawFluid(reagentTank.getFluid().getFluid(), ((float)reagentTank.getFluid().amount / (float)reagentTank.getCapacity()), this.guiLeft+27, this.guiTop+34, 16, 64);
        }
        if(resultTank.getFluid() != null) {
        	drawFluid(resultTank.getFluid().getFluid(), ((float)resultTank.getFluid().amount / (float)resultTank.getCapacity()), this.guiLeft+135, this.guiTop+34, 16, 64);
        }
        
    }
	
	
	protected void drawFluid(Fluid fluid, float amount, int x, int y, int width, int height) {
		if(fluid != null) {
			TextureMap textureMapBlocks = mc.getTextureMapBlocks();
			ResourceLocation res = fluid.getStill();
			TextureAtlasSprite spr = null;
			if (res != null) {
				spr = textureMapBlocks.getTextureExtry(res.toString());
			}
			if (spr == null) {
				spr = textureMapBlocks.getMissingSprite();
			}
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			float red = (fluid.getColor() >> 16 & 255) / 255.0F;
			float green = (fluid.getColor() >> 8 & 255) / 255.0F;
			float blue = (fluid.getColor() & 255) / 255.0F;
			GL11.glColor4f(red, green, blue, 1.0F);
			
			int startX = x;
			int startY = y + MathHelper.ceil(height * (1.0f - amount));
			int endX = x + width;
			int endY = y + height;
			
			this.drawTexturedModalRect(startX, startY, spr, width, endY - startY);
		}
	}

}
