 START 20
 MOVER AREG ='5'
 MOVER BREG ='1'
 MOVEM AREG ='5'
 MOVEM BREG A
X ORIGIN 500
 LTORG
 ORIGIN X
 MOVER CREG ='3'
 MOVER DREG ='1'
RESULT EQU X
 MOVER AREG ='5'
 MOVER DREG ='3'
 ADD CREG B
 LTORG
 MOVER BREG ='5'
 MULT BREG A
 PRINT RESULT
 STOP
A DS 4
B DC 02
 END