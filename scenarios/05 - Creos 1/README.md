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

## Without UC Abstraction solution

```
class AEntity {
  att name: string
  att ucName: double

  att communicationActive: bool
  att ucCommActive: double
  
  att location: string
  att ucLocation: double

  rel communicationMedias: ACommunicationMedia oppositeOf entities
  att ucCommunicationMedias: double //uc on the set, not on each

  rel communicationHubOf : AEntity
  att ucCommunicationHubOf: double //uc on the set, not on each
}

class AMeter extends AEntity {
  att serialNumber : string
  att ucSerialNumber: double
}

class Concentrator extends AEntity {
  att consumption: double
  att ucConsumption: double
}

class SmartMeter extends AMeter {
  att electricityActive: bool
  att ucElectricityActive: double

  att distance : int
  att ucDistance: double 

  att hops : int
  att ucHops: double

  rel routingNode: AEntity
  att ucRoutingNode: double

  rel cable: Cable
  att ucCable: double
}

class GasMeter extends AMeter {
  att gasActive : bool
  att ucGasActive: double
}

class WaterMeter extends AMeter {
  att waterActive : bool
  att ucWaterActive: double
}

class ACommunicationMedia {
  att cableID : string
  att ucCableID: double

  rel entities : AEntity oppositeOf communicationMedias
}

class AWiredCommunicationMedia extends ACommunicationMedia {}

class Cable extends AWiredCommunicationMedia {
  att material: string
  att ucMaterial: double

  att size: string
  att ucSize: double

  att remark: string
  att ucRemark: double

  att lmax: string
  att ucLmax: double

  att electricalFeeding: string
  att ucElectricalFeeding: double

  att isConnected: bool
  att ucIsConnected: double

  rel startPoint: Fuse
  att ucStartPoint: double

  rel endPoint : Fuse
  att ucEndPoint: double
}

class Cabinet {
  att name : string
  att ucName: double

  att location : string
  att ucLocation: double
  
  rel fuses : CabinetFuse oppositeOf cabinet
}

// If we want UC on each edge, we need an intermediate class 
class CabinetFuse {
    rel cabinet: Cabinet oppositeof fuses
    rel fuse: Fuse oppositeof cabinet

    att ucRelation: double

}

class Fuse {
  att closed : bool
  att ucCloded: double

  rel cabinet : CabinetFuse oppositeOf fuse

  ref cable : Cable
  att ucCable: double
}
```

## With our solution

**Generic** approach:

```
class AEntity {
  att name: Unconfident<string>
  att communicationActive: Unconfident<bool>
  att location: Unconfident<string>
  rel communicationMedias: Unconfident<ACommunicationMedia> oppositeOf entities
  rel communicationHubOf : Unconfident<AEntity>
}

class AMeter extends AEntity {
  att serialNumber : Unconfident<string>
}

class Concentrator extends AEntity {
  att consumption: Unconfident<double>
}

class SmartMeter extends AMeter {
  att electricityActive: Unconfident<bool>
  att distance : Unconfident<int>
  att hops : Unconfident<int>

  rel routingNode: Unconfident<AEntity>
  rel cable: Unconfident<Cable>
}

class GasMeter extends AMeter {
  att gasActive : Unconfident<bool>
}

class WaterMeter extends AMeter {
  att waterActive : Unconfident<bool>
}

class ACommunicationMedia {
  att cableID : Unconfident<string>

  rel entities : Unconfident<AEntity> oppositeOf communicationMedias
}

class AWiredCommunicationMedia extends ACommunicationMedia {}

class Cable extends AWiredCommunicationMedia {
  att material : Unconfident<string>
  att size : Unconfident<string>
  att remark : Unconfident<string>
  att lmax : Unconfident<string>
  att electricalFeeding : Unconfident<string>
  att isConnected : Unconfident<bool>
  rel startPoint : Unconfident<Fuse>
  rel endPoint : Unconfident<Fuse>
}

class Cabinet {
  att name : Unconfident<string>
  att location : Unconfident<string>

  @Uncertainty(value = "onEach")
  rel fuses : Unconfident<Fuse> oppositeOf cabinet
}

class Fuse {
  att closed : Unconfident<bool>

  @Uncertainty(value = "onEach")
  rel cabinet : Unconfident<Cabinet> oppositeOf fuses

  rel cable : Unconfident<Cable>
}
```

**Extension of property definition**:

```
class AEntity {
  @UCRepresentation(name = "ConfidentPerc")
  uatt name: string

  @UCRepresentation(name = "ConfidentPerc")
  uatt communicationActive: bool

  @UCRepresentation(name = "ConfidentPerc")
  uatt location: string

  @UCRepresentation(name = "ConfidentPerc")
  urel communicationMedias: ACommunicationMedia oppositeOf entities

  @UCRepresentation(name = "ConfidentPerc")
  urel communicationHubOf : AEntity
}

class AMeter extends AEntity {
  @UCRepresentation(name = "ConfidentPerc")
  uatt serialNumber : string
}

class Concentrator extends AEntity {
  @UCRepresentation(name = "ConfidentPerc")
  uatt consumption: double
}

class SmartMeter extends AMeter {
  @UCRepresentation(name = "ConfidentPerc")
  uatt electricityActive: bool

  @UCRepresentation(name = "ConfidentPerc")
  uatt distance: int

  @UCRepresentation(name = "ConfidentPerc")
  uatt hops: int

  @UCRepresentation(name = "ConfidentPerc")
  urel routingNode: AEntity

  @UCRepresentation(name = "ConfidentPerc")
  urel cable: Cable
}

class GasMeter extends AMeter {
  @UCRepresentation(name = "ConfidentPerc")
  uatt gasActive : bool
}

class WaterMeter extends AMeter {
  @UCRepresentation(name = "ConfidentPerc")
  uatt waterActive : bool
}

class ACommunicationMedia {
  @UCRepresentation(name = "ConfidentPerc")
  uatt cableID : string

  @UCRepresentation(name = "ConfidentPerc")
  urel entities : AEntity oppositeOf communicationMedias
}

class AWiredCommunicationMedia extends ACommunicationMedia {}

class Cable extends AWiredCommunicationMedia {
  @UCRepresentation(name = "ConfidentPerc")
  uatt material : string

  @UCRepresentation(name = "ConfidentPerc")
  uatt size : string

  @UCRepresentation(name = "ConfidentPerc")
  uatt remark : string

  @UCRepresentation(name = "ConfidentPerc")
  uatt lmax : string

  @UCRepresentation(name = "ConfidentPerc")
  uatt electricalFeeding : string

  @UCRepresentation(name = "ConfidentPerc")
  uatt isConnected : bool

  @UCRepresentation(name = "ConfidentPerc")
  urel startPoint : Fuse

  @UCRepresentation(name = "ConfidentPerc")
  urel endPoint : Fuse
}

class Cabinet {
  @UCRepresentation(name = "ConfidentPerc")
  uatt name : string

  @UCRepresentation(name = "ConfidentPerc")
  uatt location : string

  @UCRepresentation(name = "ConfidencePerc", param = "onEach" )
  urel fuses : Fuse oppositeOf cabinet
}

class Fuse {
  @UCRepresentation(name = "ConfidentPerc")
  uatt closed: bool

  @UCRepresentation(name = "ConfidencePerc", param = "onEach" )
  urel cabinet: Cabinet oppositeOf fuses

  @UCRepresentation(name = "ConfidencePerc")
  urel cable: Unconfident<Cable>
}
```