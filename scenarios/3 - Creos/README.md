# Creos scenario - Simplified

**Disclaimer:** The scenario has been simplified by applying only a percentage representation. 
But we will complexify it during our progress.

This scenario is based on the case study of a project between [SnT Luxembourg](https://wwwfr.uni.lu/snt) and [Creos Luxembourg S.A.](http://www.creos-net.lu/start.html).

The network topology is mainly manually by Creos technicians. 
However, as everyone knows, human errors are frequent.
The stored network may not represent the real situation: the network topology is uncertain.
Moreover, to connect or disconnect some cable, there is some cabinet with fuses. 
These fuses are manually modified and the modification is also manually to the system.
It adds thus an uncertainty on the state of the different fuses and it increases the uncertainty on the network.

Without appropriate techniques to represent this uncertainty, it is either ignore or manually managed by a developer.
Let us imagine that this uncertainty is manually managed.
The model will look like:

```
class AEntity {
  att name: String
  att ucName: double

  att communicationActive: Bool
  att ucCommActive: double
  
  att location: String
  att ucLocation: double

  rel communicationMedias: ACommunicationMedia oppositeOf entities
  att ucCommunicationMedias: double //uc on the set, not on each

  rel communicationHubOf : AEntity
  att ucCommunicationHubOf: double //uc on the set, not on each
}

class AMeter extends AEntity {
  att serialNumber : String
  att ucSerialNumber: double
}

class Concentrator extends AEntity {
  att consumption: double
  att ucConsumption: double
}

class SmartMeter extends AMeter {
  att electricityActive: Bool
  att ucElectricityActive: double

  att distance : int
  att ucDistance: double 

  att hops : int
  att ucHops: double

  ref routingNode: AEntity
  att ucRoutingNode: double

  ref cable: Cable
  att ucCable: double
}

class GasMeter extends AMeter {
  att gasActive : Bool
  att ucGasActive: double
}

class WaterMeter extends AMeter {
  att waterActive : Bool
  att ucWaterActive: double
}

class ACommunicationMedia {
  att cableID : String
  att ucCableID: double

  rel entities : AEntity oppositeOf communicationMedias
}

class AWiredCommunicationMedia extends ACommunicationMedia {}

class Cable extends AWiredCommunicationMedia {
  att material: String
  att ucMaterial: double

  att size: String
  att ucSize: double

  att remark: String
  att ucRemark: double

  att lmax: String
  att ucLmax: double

  att electricalFeeding: String
  att ucElectricalFeeding: double

  att isConnected: Bool
  att ucIsConnected: double

  ref startPoint: Fuse
  att ucStartPoint: double

  ref endPoint : Fuse
  att ucEndPoint: double
}

class Cabinet {
  att name : String
  att ucName: double

  att location : String
  att ucLocation: double
  
  rel fuses : CabinetFuse oppositeOf cabinet
}

// If we want UC on each edge, we need an intermediate class 
class CabinetFuse {
    ref cabinet: Cabinet oppositeof fuses
    ref fuse: Fuse oppositeof cabinet

    att ucRelation: double

}

class Fuse {
  att closed : Bool
  att ucCloded: double

  ref cabinet : CabinetFuse oppositeOf fuse

  ref cable : Cable
  att ucCable: double
}
```

Using our approach:

```
class AEntity {
  att name: UncertainPerc<String>
  att communicationActive: UncertainPerc<Bool>
  att location: UncertainPerc<String>
  rel communicationMedias: UncertainGlobal<ACommunicationMedia> oppositeOf entities
  rel communicationHubOf : UncertainGlobal<AEntity>
}

class AMeter extends AEntity {
  att serialNumber : UncertainPerc<String>
}

class Concentrator extends AEntity {
  att consumption: UncertainPerc<double>
}

class SmartMeter extends AMeter {
  att electricityActive: UncertainPerc<Bool>
  att distance : UncertainPerc<int>
  att hops : UncertainPerc<int>

  ref routingNode: Uncertain<AEntity>
  ref cable: Uncertain<Cable>
}

class GasMeter extends AMeter {
  att gasActive : UncertainPerc<Bool>
}

class WaterMeter extends AMeter {
  att waterActive : UncertainPerc<Bool>
}

class ACommunicationMedia {
  att cableID : UncertainPerc<String>

  rel entities : UncertainGlobal<AEntity> oppositeOf communicationMedias
}

class AWiredCommunicationMedia extends ACommunicationMedia {}

class Cable extends AWiredCommunicationMedia {
  att material : UncertainPerc<String>
  att size : UncertainPerc<String>
  att remark : UncertainPerc<String>
  att lmax : UncertainPerc<String>
  att electricalFeeding : UncertainPerc<String>
  att isConnected : UncertainPerc<Bool>
  ref startPoint : Uncertain<Fuse>
  ref endPoint : Uncertain<Fuse>
}

class Cabinet {
  att name : UncertainPerc<String>
  att location : UncertainPerc<String>
  rel fuses : Uncertain<Fuse> oppositeOf cabinet
}

class Fuse {
  att closed : Bool
  att ucCloded: double

  ref cabinet : Uncertain<Cabinet> oppositeOf fuse

  ref cable : Uncertain<Cable>
}
```


