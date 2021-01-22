package Interfaces;

import pt.iul.ista.poo.farm.objects.FarmObject;
import pt.iul.ista.poo.farm.objects.Farmer;

public interface Interactable{
	void interact(FarmObject fo,int key);
	int getImportancia();
}
