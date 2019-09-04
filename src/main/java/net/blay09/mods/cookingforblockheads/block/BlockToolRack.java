package net.blay09.mods.cookingforblockheads.block;

import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.tile.ToolRackTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockToolRack extends BlockKitchen {

    public static final String name = "tool_rack";
    public static final ResourceLocation registryName = new ResourceLocation(CookingForBlockheads.MOD_ID, name);

    private static final VoxelShape[] BOUNDING_BOXES = new VoxelShape[]{
            Block.makeCuboidShape(0, 0.25, 1 - 0.125, 1, 1, 1),
            Block.makeCuboidShape(0, 0.25, 0, 1, 1, 0.125),
            Block.makeCuboidShape(1 - 0.125, 0.25, 0, 1, 1, 1),
            Block.makeCuboidShape(0, 0.25, 0, 0.125, 1, 1),
    };

    public BlockToolRack() {
        super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(2.5f), registryName);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ToolRackTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        Direction facing = state.get(FACING);
        return BOUNDING_BOXES[facing.getIndex() - 2];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        return VoxelShapes.empty();
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction facing = context.getFace();
        if (facing == Direction.UP || facing == Direction.DOWN) {
            facing = Direction.NORTH;
        }

        return getDefaultState().with(FACING, facing);
    }


    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (hand != Hand.MAIN_HAND) {
            return true;
        }

        ItemStack heldItem = player.getHeldItem(hand);
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof BlockItem) {
            return true;
        }

        if (rayTraceResult.getHitVec().y > 0.25f) {
            Direction stateFacing = state.get(FACING);
            double hit = rayTraceResult.getHitVec().x;
            switch (stateFacing) {
                case NORTH:
                    hit = rayTraceResult.getHitVec().x;
                    break;
                case SOUTH:
                    hit = 1f - rayTraceResult.getHitVec().x;
                    break;
                case WEST:
                    hit = 1f - rayTraceResult.getHitVec().z;
                    break;
                case EAST:
                    hit = rayTraceResult.getHitVec().z;
                    break;
            }

            int hitSlot = hit > 0.5f ? 0 : 1;
            ToolRackTileEntity tileToolRack = (ToolRackTileEntity) world.getTileEntity(pos);
            if (tileToolRack != null) {
                if (!heldItem.isEmpty()) {
                    ItemStack oldToolItem = tileToolRack.getItemHandler().getStackInSlot(hitSlot);
                    ItemStack toolItem = heldItem.split(1);
                    if (!oldToolItem.isEmpty()) {
                        if (!player.inventory.addItemStackToInventory(oldToolItem)) {
                            player.dropItem(oldToolItem, false);
                        }
                        tileToolRack.getItemHandler().setStackInSlot(hitSlot, toolItem);
                    } else {
                        tileToolRack.getItemHandler().setStackInSlot(hitSlot, toolItem);
                    }
                } else {
                    ItemStack itemStack = tileToolRack.getItemHandler().getStackInSlot(hitSlot);
                    if (!itemStack.isEmpty()) {
                        tileToolRack.getItemHandler().setStackInSlot(hitSlot, ItemStack.EMPTY);
                        player.setHeldItem(hand, itemStack);
                    }
                }
                return true;
            }
        }

        return true;
    }

}
