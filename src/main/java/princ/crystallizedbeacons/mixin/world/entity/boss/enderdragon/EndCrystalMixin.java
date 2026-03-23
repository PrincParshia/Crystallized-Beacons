package princ.crystallizedbeacons.mixin.world.entity.boss.enderdragon;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import princ.crystallizedbeacons.mixin.world.level.block.entity.BeaconBlockEntityAccessor;
import princ.crystallizedbeacons.util.world.entity.boss.enderdragon.EndCrystalAccessor;
import princ.crystallizedbeacons.network.protocol.world.entity.boss.enderdragon.EndCrystalS2CPayload;

import java.util.Comparator;
import java.util.List;

@Mixin(EndCrystal.class)
public class EndCrystalMixin implements EndCrystalAccessor {
    @Unique
    @Nullable
    private Entity crystallizedBeacons$beamTarget;

    @Inject(method = "tick", at = @At("TAIL"))
    void tick(CallbackInfo callbackInfo) {
        EndCrystal endCrystal = (EndCrystal) (Object) this;
        Level level = endCrystal.level();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockPos blockPos = endCrystal.blockPosition().below();

        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(serverLevel.getBlockState(blockPos).getBlock() instanceof BeaconBlock) ||
                !(serverLevel.getBlockEntity(blockPos) instanceof BeaconBlockEntity beaconBlockEntity)) return;

        int int_ = ((BeaconBlockEntityAccessor) beaconBlockEntity).levels();
        if (int_ <= 0) return;

        double totalWeight = 0;
        int totalBlocks = 0;

        for (int i = 1; i <= int_; i++) {
            int y = blockPos.getY() - i;
            for (int x = -i; x <= i; x++) {
                for (int z = -i; z <= i; z++) {
                    Block block = serverLevel.getBlockState(
                            mutableBlockPos.set(blockPos.getX() + x, y, blockPos.getZ() + z)
                    ).getBlock();

                    totalWeight += this.getMaterialWeight(block, int_);
                    totalBlocks++;
                }
            }
        }

        double double_ = 16 * int_;
        double d = totalBlocks == 0 ? 0 : totalWeight / totalBlocks;
        List<Mob> enemies = serverLevel.getEntitiesOfClass(Mob.class, endCrystal.getBoundingBox().inflate(double_), enemy -> {
            if (d < 0.75 && (enemy instanceof WitherBoss || enemy instanceof Warden)) {
                return false;
            }

            return enemy instanceof Enemy && !(enemy instanceof EnderDragon) && enemy.isAlive() && !enemy.isInvulnerable();
        });
        Mob enemy = enemies.stream().min(Comparator.comparingDouble(m -> m.distanceToSqr(endCrystal))).orElse(null);
        Mob previousClosestEnemy = (Mob) this.crystallizedBeacons$beamTarget;
        Mob closestEnemy;

        if (enemy != null) {
            closestEnemy = enemy;
        } else if (previousClosestEnemy != null && previousClosestEnemy.isAlive()) {
            closestEnemy = previousClosestEnemy;
        } else {
            closestEnemy = null;
        }

        if (this.crystallizedBeacons$beamTarget != closestEnemy) {
            crystallizedBeacons$sendToClients(endCrystal, closestEnemy);
        }
        this.crystallizedBeacons$beamTarget = closestEnemy;

        if (enemy != null) {
            int i = switch (int_) {
                case 2 -> 17;
                case 3 -> 13;
                case 4 -> 10;
                default -> 20;
            };

            if (endCrystal.tickCount % i == 0) {
                DamageSource damageSource = serverLevel.damageSources().generic();
                float f = (float) (4 + d * 16);
                enemy.hurtServer(serverLevel, damageSource, f);
            }
        }
    }

    @Unique
    private static void crystallizedBeacons$sendToClients(EndCrystal endCrystal, @Nullable Entity entity) {
        PlayerLookup.tracking(endCrystal).forEach(player -> {
            if (ServerPlayNetworking.canSend(player, EndCrystalS2CPayload.TYPE)) {
                ServerPlayNetworking.send(player, new EndCrystalS2CPayload(endCrystal.getId(), entity == null ? -1 : entity.getId()));
            }
        });
    }

    @Unique
    double getMaterialWeight(Block block, int i) {
        double d;

        if (block == Blocks.IRON_BLOCK) d = 0.0;
        else if (block == Blocks.GOLD_BLOCK) d = 0.25;
        else if (block == Blocks.EMERALD_BLOCK) d = 0.5;
        else if (block == Blocks.DIAMOND_BLOCK) d = 0.75;
        else if (block == Blocks.NETHERITE_BLOCK) d = 1.0;
        else return 0;

        return d * ((double) i / 4);
    }

    @Override
    public @Nullable Entity crystallizedBeacons$getBeamTarget() {
        return this.crystallizedBeacons$beamTarget;
    }

    @Override
    public void crystallizedBeacons$setBeamTarget(@Nullable Entity entity) {
        this.crystallizedBeacons$beamTarget = entity;
    }
}
