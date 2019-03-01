<h3>REST API - todo in backend</h3>
Frontend part (angularjs) should be used from 
<a href="https://github.com/hyperon-io/motor-insurance-advanced">github example</a>. It communicates with backend using multiple REST endpoints. Developer should provide implementation for all endpoints defined below.
</p>
<h5>Vehicle - endpoints</h5>
<ul>
<li>/vehicle/productionYear - PUT method
    <p>Description: Provide vehicle production year</p>
    Request Body:
    <ul>
        <li>productionYear - Long</li>
    </ul>
</li>
<li>/vehicle/make - PUT method
    <p>Description: Provide manufacturer ID</p>
    Request Body:
    <ul>
        <li>makeId - Long</li>
    </ul>
</li>
<li>/vehicle/model - PUT method
    <p>Description: Provide vehicle model ID</p>
    Request Body:
    <ul>
        <li>modelId - Long</li>
    </ul>
</li>
<li>/vehicle/type - PUT method
    <p>Description: Provide vehicle type ID</p>
    Request Body:
    <ul>
        <li>typeId - Long</li>
    </ul>
</li>
</ul> 
<h5>Dictionary - endpoints</h5>
<ul>
<li>/dictionaries/make - GET method
     <p>Description: Get manufacturer data based on vehicle production year</p>
    Request parameter:
    <ul>
        <li>productionYear - Integer</li>
    </ul>
</li>
<li>/dictionaries/model - GET method
    <p>Description: Get vehicle model data based on vehicle type id</p>
    Request parameter:
    <ul>
        <li>typeId - Integer</li>
    </ul>
</li>
<li>/dictionaries/productionYear - GET method
    <p>Description: Get production year</p>
</li>
<li>/dictionaries/type - GET method
    <p>Description: Get vehicle type data based on manufacturer id</p>
    Request parameter:
    <ul>
        <li>makeId - Integer</li>
    </ul>
</li>
</ul>
<h5>Driver - endpoints</h5>
<ul>
<li>/driver/gender - PUT method
    <p>Description: Provide driver's gender</p>
    Request Body:
    <ul>
        <li>gender - String</li>
    </ul>
</li>
<li>/driver/birthDate - PUT method
    <p>Description: Provide driver's birth date</p>
    Request Body:
    <ul>
        <li>birthDate - Date</li>
    </ul>
</li>
<li>/driver/firstName - PUT method
    <p>Description: Provide driver's first name</p>
    Request Body:
    <ul>
        <li>firstName - String</li>
    </ul>
</li>
<li>/driver/lastName - PUT method
    <p>Description: Provide driver's last name</p>
    Request Body:
    <ul>
        <li>lastName - String</li>
    </ul>
</li>
<li>/driver/accidentCount - PUT method
    <p>Description: Provide driver's number of accidents</p>
    Request Body:
    <ul>
        <li>accidentCount - Integer</li>
    </ul>
</li>
<li>/driver/trafficTicketsCount - PUT method
    <p>Description: Provide driver's number of traffic tickets</p>
    Request Body:
    <ul>
        <li>trafficTicketsCount - Integer</li>
    </ul>
</li>
<li>/driver/licenceObtainedAtAge - PUT method
    <p>Description: Provide driver's age, when he or she obtained licence</p>
    Request Body:
    <ul>
        <li>licenceObtainedAtAge - Integer</li>
    </ul>
</li>
<li>/driver/address/zipCode - PUT method
    <p>Description: Provide driver's zip code</p>
    Request Body:
    <ul>
        <li>zipCode - String</li>
    </ul>
</li>
<li>/driver/address/city - PUT method
    <p>Description: Provide driver's city, where he or she lives</p>
    Request Body:
    <ul>
        <li>city - String</li>
    </ul>
</li>
<li>/driver/address/street - PUT method
    <p>Description: Provide driver's street, on which he or she lives</p>
    Request Body:
    <ul>
        <li>street - String</li>
    </ul>
</li>
<h5>Quote - endpoints</h5>
<ul>
<li>/quote - GET method - this should trigger recalculation</li>
</ul>