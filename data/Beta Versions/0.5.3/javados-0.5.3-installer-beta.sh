echo ################################
echo # JavaDOS 0.5.3 Beta installer #
echo ################################
echo Starting installation...

if [[ $EUID -ne 0 ]]; then
   echo "Installer must be run as root, exiting..."
   exit 1
fi

echo "Updating repositories..."
sudo apt update
sudo apt upgrade
echo "Installing LightDM..."
sudo apt install lightdm
echo "Installing OpenJDK 17..."
sudo apt install openjdk-17-jdk
echo "Copying JavaDOS files..."
sudo mkdir /var/JavaDOS
sudo rsync -r -av --exclude='./javados-0.5.3-installer-beta.sh' ./* /var/JavaDOS/
echo "Configuring JavaDOS startup..."
sudo touch /etc/lightdm/lightdm.conf
sudo echo "[Seat:*]" >> /etc/lightdm/lightdm.conf
sudo echo "greeter-setup-script=/usr/bin/sudo java -jar /var/JavaDOS/bin/JDOS_0.5.3.jar --full-screen" >> /etc/lightdm/lightdm.conf
echo "Installation done. Please reboot to enter JavaDOS console."
exit 0
