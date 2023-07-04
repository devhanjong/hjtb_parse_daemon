package kr.co.devhanjong.coin_parse_daemon.memorydb;

import kr.co.devhanjong.coin_parse_daemon.dto.StreamHogaDto;
import kr.co.devhanjong.coin_parse_daemon.model.HogaMemoryDbKey;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class HogaMemoryDb {
    private final ConcurrentHashMap<HogaMemoryDbKey, StreamHogaDto> hogaDb = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<HogaMemoryDbKey, ReentrantLock> lockDb = new ConcurrentHashMap<>();

    public void addHoga(String vaspSimpleName, String mySymbol, StreamHogaDto hoga) {
        HogaMemoryDbKey hogaMemoryDbKey = HogaMemoryDbKey.builder()
                .vaspSimpleName(vaspSimpleName)
                .mySymbol(mySymbol)
                .build();
        hogaDb.put(hogaMemoryDbKey, hoga);
    }

    public StreamHogaDto getHoga(String vaspSimpleName, String mySymbol) {
        HogaMemoryDbKey hogaMemoryDbKey = HogaMemoryDbKey.builder()
                .vaspSimpleName(vaspSimpleName)
                .mySymbol(mySymbol)
                .build();
        return hogaDb.get(hogaMemoryDbKey);
    }

    public void removeHoga(String vaspSimpleName, String mySymbol) {
        HogaMemoryDbKey hogaMemoryDbKey = HogaMemoryDbKey.builder()
                .vaspSimpleName(vaspSimpleName)
                .mySymbol(mySymbol)
                .build();
        hogaDb.remove(hogaMemoryDbKey);
    }

    public void lock(String vaspSimpleName, String mySymbol){
        HogaMemoryDbKey hogaMemoryDbKey = HogaMemoryDbKey.builder()
                .vaspSimpleName(vaspSimpleName)
                .mySymbol(mySymbol)
                .build();

        if(!lockDb.containsKey(hogaMemoryDbKey)){
            lockDb.put(hogaMemoryDbKey, new ReentrantLock());
        }

        lockDb.get(hogaMemoryDbKey).lock();
    }

    public void unlock(String vaspSimpleName, String mySymbol){
        HogaMemoryDbKey hogaMemoryDbKey = HogaMemoryDbKey.builder()
                .vaspSimpleName(vaspSimpleName)
                .mySymbol(mySymbol)
                .build();

        if(lockDb.containsKey(hogaMemoryDbKey)){
            lockDb.get(hogaMemoryDbKey).unlock();
        }
    }
}
