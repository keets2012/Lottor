package com.blueskykong.lottor.common.netty;

import com.blueskykong.lottor.common.enums.SerializeProtocolEnum;
import com.blueskykong.lottor.common.netty.serizlize.hessian.HessianCodecServiceImpl;
import com.blueskykong.lottor.common.netty.serizlize.hessian.HessianDecoder;
import com.blueskykong.lottor.common.netty.serizlize.hessian.HessianEncoder;
import com.blueskykong.lottor.common.netty.serizlize.kryo.KryoCodecServiceImpl;
import com.blueskykong.lottor.common.netty.serizlize.kryo.KryoDecoder;
import com.blueskykong.lottor.common.netty.serizlize.kryo.KryoEncoder;
import com.blueskykong.lottor.common.netty.serizlize.kryo.KryoPoolFactory;
import com.blueskykong.lottor.common.netty.serizlize.protostuff.ProtostuffCodecServiceImpl;
import com.blueskykong.lottor.common.netty.serizlize.protostuff.ProtostuffDecoder;
import com.blueskykong.lottor.common.netty.serizlize.protostuff.ProtostuffEncoder;
import io.netty.channel.ChannelPipeline;


public class NettyPipelineInit {
    public static void serializePipeline(SerializeProtocolEnum serializeProtocol, ChannelPipeline pipeline) {
        switch (serializeProtocol) {
            case KRYO:
                KryoCodecServiceImpl kryoCodecServiceImpl = new KryoCodecServiceImpl(KryoPoolFactory.getKryoPoolInstance());
                pipeline.addLast(new KryoEncoder(kryoCodecServiceImpl));
                pipeline.addLast(new KryoDecoder(kryoCodecServiceImpl));
                break;
            case HESSIAN:
                HessianCodecServiceImpl hessianCodecServiceImpl = new HessianCodecServiceImpl();
                pipeline.addLast(new HessianEncoder(hessianCodecServiceImpl));
                pipeline.addLast(new HessianDecoder(hessianCodecServiceImpl));
                break;
            case PROTOSTUFF:
                ProtostuffCodecServiceImpl protostuffCodecServiceImpl = new ProtostuffCodecServiceImpl();
                pipeline.addLast(new ProtostuffEncoder(protostuffCodecServiceImpl));
                pipeline.addLast(new ProtostuffDecoder(protostuffCodecServiceImpl));
                break;
            default:
                KryoCodecServiceImpl defaultCodec = new KryoCodecServiceImpl(KryoPoolFactory.getKryoPoolInstance());
                pipeline.addLast(new KryoEncoder(defaultCodec));
                pipeline.addLast(new KryoDecoder(defaultCodec));
                break;
        }
    }
}
