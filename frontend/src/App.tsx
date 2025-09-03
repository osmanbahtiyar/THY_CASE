import {
  Box,
  Flex,
  Heading,
  HStack,
  VStack,
  Link as CLink,
  Container,
  Image,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalCloseButton,
  ModalBody,
  ModalFooter,
  Button,
  Text,
} from "@chakra-ui/react";
import { useState } from "react";
import { NavLink, Routes, Route } from "react-router-dom";
import LocationsPage from "./pages/LocationsPage";
import TransportationsPage from "./pages/TransportationsPage";
import RoutesPage from "./pages/RoutesPage";
import logo from "./assets/logo.svg";

function Sidebar() {
  return (
    <Box
      as="nav"
  w="350px"
  minH="100dvh"
  bg="pantone7620.500"
  color="white"
  p={4}
  borderRight="3px solid"
  borderColor="gray.200"
    >
      <HStack spacing={3} mb={6}>
  <Image
  src={logo}
  alt="THY Logo"
  h="80px"          
  w="auto"          
  mx="auto"
  mb={6}            
/>
</HStack>
      <VStack align="stretch" spacing={4}>
        <CLink
          as={NavLink}
          to="/locations"
          fontSize="lg"
          fontWeight="bold"
          px={3}
          py={2}
          border="2px solid"
          borderColor="white"
          borderRadius="md"
          _hover={{ bg: "blue", color: "pantone7620" }}
        >
          Locations
        </CLink>

        <CLink
          as={NavLink}
          to="/transportations"
          fontSize="lg"
          fontWeight="bold"
          px={3}
          py={2}
          border="2px solid"
          borderColor="white"
          borderRadius="md"
          _hover={{ bg: "blue", color: "pantone7620" }}
        >
          Transportations
        </CLink>

        <CLink
          as={NavLink}
          to="/routes"
          fontSize="lg"
          fontWeight="bold"
          px={3}
          py={2}
          border="2px solid"
          borderColor="white"
          borderRadius="md"
          _hover={{ bg: "blue", color: "pantone7620" }}
        >
          Routes
        </CLink>
      </VStack>
    </Box>
  );
}

function Home() {
  return (
    <Container maxW="6xl" py={8}>
      <Heading size="md" mb={2}>Welcome 👋</Heading>
      <Box color="gray.600">Menüden bir sayfa seç.</Box>
    </Container>
  );
}


export default function App() {
  const [globalError, setGlobalError] = useState<{ title?: string; description?: string; violations?: string[] } | null>(null);
  const showError = (err: string | { title?: string; description?: string; violations?: string[] }) => {
    if (typeof err === "string") setGlobalError({ title: "Error", description: err });
    else setGlobalError({ title: err.title || "Error", description: err.description, violations: err.violations });
  };
  return (
    <Flex>
      <Sidebar />
      <Box flex="1">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/locations" element={<LocationsPage showError={showError} />} />
          <Route path="/transportations" element={<TransportationsPage showError={showError} />} />
          <Route path="/routes" element={<RoutesPage showError={showError} />} />
        </Routes>
        <Modal isOpen={!!globalError} onClose={() => setGlobalError(null)} isCentered>
          <ModalOverlay />
          <ModalContent>
            <ModalHeader>{globalError?.title || "Error"}</ModalHeader>
            <ModalCloseButton />
            <ModalBody>
              <VStack align="stretch" spacing={3}>
                <Text color="red.600">{globalError?.description || "An unexpected error occurred."}</Text>
                {Array.isArray(globalError?.violations) && globalError!.violations!.length > 0 && (
                  <Box>
                    <Text fontWeight="semibold" mb={1}>Details</Text>
                    <VStack as="ul" align="stretch" spacing={1} pl={4}>
                      {globalError!.violations!.map((msg, i) => (
                        <Text as="li" key={i} color="gray.700">• {msg}</Text>
                      ))}
                    </VStack>
                  </Box>
                )}
              </VStack>
            </ModalBody>
            <ModalFooter>
              <Button onClick={() => setGlobalError(null)} colorScheme="red">Close</Button>
            </ModalFooter>
          </ModalContent>
        </Modal>
      </Box>
    </Flex>
  );
}